/*******************************************************************************
 * Copyright (c) 2019, 2021 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.transaction.test.dbrotationtests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.testcontainers.containers.JdbcDatabaseContainer;

import com.ibm.websphere.simplicity.log.Log;
import com.ibm.ws.transaction.test.tests.DualServerDynamicCoreTest2;
import com.ibm.ws.transaction.web.Simple2PCCloudServlet;

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.topology.impl.LibertyServer;

@Mode
@RunWith(FATRunner.class)
public class DualServerDynamicDBRotationTest2 extends DualServerDynamicCoreTest2 {

    private static final int LOG_SEARCH_TIMEOUT = 120000;

    @Server("com.ibm.ws.transaction_ANYDBCLOUD001")
    @TestServlet(servlet = Simple2PCCloudServlet.class, contextRoot = APP_NAME)
    public static LibertyServer firstServer;

    @Server("com.ibm.ws.transaction_ANYDBCLOUD002")
    @TestServlet(servlet = Simple2PCCloudServlet.class, contextRoot = APP_NAME)
    public static LibertyServer secondServer;

    public static JdbcDatabaseContainer<?> testContainer;

    @Override
    public void setUp(LibertyServer server) throws Exception {
        setupDriver(server);
    }

    @BeforeClass
    public static void setUp() throws Exception {
        setup(firstServer, secondServer, "Simple2PCCloudServlet", "cloud001");
    }

    @Override
    public void dynamicTest(LibertyServer server1, LibertyServer server2, int test, int resourceCount) throws Exception {
        final String method = "dynamicTest";
        final String id = String.format("%03d", test);
        StringBuilder sb = null;
        Log.info(this.getClass(), method, "Starting dynamic test in DualServerDynamicDBRotationTest");
        // Start Server1
        startServers(server1);
        Log.info(this.getClass(), method, "now invoke runTestWithResponse from DualServerDynamicDBRotationTest");
        try {
            // We expect this to fail since it is gonna crash the server
            sb = runTestWithResponse(server1, servletName, "setupRec" + id);
        } catch (Throwable e) {
        }
        Log.info(this.getClass(), method, "back from runTestWithResponse in DualServerDynamicDBRotationTest, sb is " + sb);
        assertNull("setupRec" + id + " returned: " + sb, sb);

        Log.info(this.getClass(), method, "wait for first server to go away in DualServerDynamicDBRotationTest");
        // wait for 1st server to have gone away
        assertNotNull(server1.getServerName() + " did not crash", server1.waitForStringInLog("Dump State:"));

        // Now start server2
        server2.setHttpDefaultPort(Cloud2ServerPort);
        startServers(server2);

        // wait for 2nd server to perform peer recovery
        assertNotNull(server2.getServerName() + " did not perform peer recovery",
                      server2.waitForStringInTrace("Performed recovery for " + cloud1RecoveryIdentity, LOG_SEARCH_TIMEOUT));

        // flush the resource states
        try {
            sb = runTestWithResponse(server2, servletName, "dumpState");
            Log.info(this.getClass(), method, sb.toString());
        } catch (Exception e) {
            Log.error(this.getClass(), method, e);
            fail(e.getMessage());
        }

        //Stop server2
        server2.stopServer((String[]) null);

        // restart 1st server
        server1.resetStarted();
        startServers(server1);

        assertNotNull("Recovery incomplete on " + server1.getServerName(), server1.waitForStringInTrace("WTRN0133I"));

        // check resource states
        Log.info(this.getClass(), method, "calling checkRec" + id);
        try {
            sb = runTestWithResponse(server1, servletName, "checkRec" + id);
        } catch (Exception e) {
            Log.error(this.getClass(), "dynamicTest", e);
            throw e;
        }
        Log.info(this.getClass(), method, "checkRec" + id + " returned: " + sb);

        // Bounce first server to clear log
        server1.stopServer((String[]) null);
        startServers(server1);

        // Check log was cleared
        assertNotNull("Transactions left in transaction log on " + server1.getServerName(), server1.waitForStringInTrace("WTRN0135I"));
        assertNotNull("XAResources left in partner log on " + server1.getServerName(), server1.waitForStringInTrace("WTRN0134I.*0"));
    }

    @After
    public void tearDown() throws Exception {
        tidyServersAfterTest(server1, server2);
    }
}
