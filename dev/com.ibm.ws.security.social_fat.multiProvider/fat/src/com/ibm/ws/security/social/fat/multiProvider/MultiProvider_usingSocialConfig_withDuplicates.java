/*******************************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.social.fat.multiProvider;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.log.Log;
import com.ibm.ws.security.oauth_oidc.fat.commonTest.RSCommonTestTools;
import com.ibm.ws.security.social.fat.multiProvider.commonTests.Social_MultiProvider_BasicTests;
import com.ibm.ws.security.social.fat.utils.SocialConstants;
import com.ibm.ws.security.social.fat.utils.SocialTestSettings;

import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServerWrapper;

/**
 *
 **/
@RunWith(FATRunner.class)
@LibertyServerWrapper
@Mode(TestMode.FULL)
public class MultiProvider_usingSocialConfig_withDuplicates extends Social_MultiProvider_BasicTests {

    public static Class<?> thisClass = MultiProvider_usingSocialConfig_withDuplicates.class;

    public static RSCommonTestTools rsTools = new RSCommonTestTools();

    @BeforeClass
    public static void setUp() throws Exception {
        classOverrideValidationEndpointValue = SocialConstants.USERINFO_ENDPOINT;

        List<String> startMsgs = new ArrayList<String>();
        startMsgs.add("CWWKT0016I.*" + SocialConstants.SOCIAL_DEFAULT_CONTEXT_ROOT);

        List<String> extraApps = new ArrayList<String>();
        extraApps.add(SocialConstants.HELLOWORLD_SERVLET);

        // TODO fix
        List<String> opStartMsgs = new ArrayList<String>();
        //        opStartMsgs.add("CWWKS1600I.*" + SocialConstants.OIDCCONFIGMEDIATOR_APP);
        opStartMsgs.add("CWWKS1631I.*");

        // TODO fix
        List<String> opExtraApps = new ArrayList<String>();
        opExtraApps.add(SocialConstants.OP_SAMPLE_APP);

        String[] propagationTokenTypes = rsTools.chooseTokenSettings(SocialConstants.OIDC_OP);
        String tokenType = propagationTokenTypes[0];
        String certType = propagationTokenTypes[1];
        Log.info(thisClass, "setupBeforeTest", "inited tokenType to: " + tokenType);

        socialSettings = new SocialTestSettings();
        testSettings = socialSettings;

        testOPServer = commonSetUp(SocialConstants.SERVER_NAME + ".multiProvider.op", "op_server_orig.xml", SocialConstants.OIDC_OP, null, SocialConstants.DO_NOT_USE_DERBY, opStartMsgs, null, SocialConstants.OIDC_OP, true, true, tokenType, certType);
        genericTestServer = commonSetUp(SocialConstants.SERVER_NAME + ".multiProvider.social", "server_multiProvider_usingSocialConfig_withDups.xml", SocialConstants.GENERIC_SERVER, extraApps, SocialConstants.DO_NOT_USE_DERBY, startMsgs);

        setGenericVSSpeicificProviderFlags(GenericConfig);

        nameExtender = "2";
    }

}
