/*******************************************************************************
 * Copyright (c) 2016, 2022 IBM Corporation and others.
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
package com.ibm.ws.security.jwt.internal;

import java.security.AccessController;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.ibm.websphere.ras.Tr;
import com.ibm.websphere.ras.TraceComponent;
import com.ibm.websphere.ras.annotation.Sensitive;
import com.ibm.ws.ffdc.annotation.FFDCIgnore;
import com.ibm.ws.security.common.jwk.impl.JWKSet;
import com.ibm.ws.security.jwt.config.ConsumerUtils;
import com.ibm.ws.security.jwt.config.JwtConfigUtil;
import com.ibm.ws.security.jwt.config.JwtConsumerConfig;
import com.ibm.ws.security.jwt.utils.JwtUtils;
import com.ibm.ws.ssl.KeyStoreService;
import com.ibm.wsspi.kernel.service.utils.AtomicServiceReference;
import com.ibm.wsspi.ssl.SSLSupport;

@Component(service = JwtConsumerConfig.class, immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = "com.ibm.ws.security.jwt.consumer", name = "jwtConsumerConfig", property = "service.vendor=IBM")
public class JwtConsumerConfigImpl implements JwtConsumerConfig {

    private static final TraceComponent tc = Tr.register(JwtConsumerConfigImpl.class);

    private String id;
    private String issuer = null;
    @Sensitive
    private String sharedKey;
    private List<String> audiences;
    private String sigAlg;
    private String trustStoreRef;
    private String trustedAlias;
    private long clockSkewMilliSeconds;
    private boolean jwkEnabled;
    private String jwkEndpointUrl;
    private boolean validationRequired = true;
    private boolean useSystemPropertiesForHttpClientConnections = false;
    private List<String> amrClaim;
    private String keyManagementKeyAlias;
    String sslRef;

    private ConsumerUtils consumerUtil = null; // init during process(activate
                                               // and modify)
    private JWKSet jwkSet = null; // lazy init

    /***********************************
     * Begin OSGi-related fields and methods
     ***********************************/

    public static final String KEY_KEYSTORE_SERVICE = "keyStoreService";
    private final AtomicServiceReference<KeyStoreService> keyStoreServiceRef = new AtomicServiceReference<KeyStoreService>(
            KEY_KEYSTORE_SERVICE);

    @Reference(service = KeyStoreService.class, name = KEY_KEYSTORE_SERVICE, policy = ReferencePolicy.DYNAMIC, cardinality = ReferenceCardinality.OPTIONAL, policyOption = ReferencePolicyOption.GREEDY)
    protected void setKeyStoreService(ServiceReference<KeyStoreService> ref) {
        keyStoreServiceRef.setReference(ref);
    }

    protected void unsetKeyStoreService(ServiceReference<KeyStoreService> ref) {
        keyStoreServiceRef.unsetReference(ref);
    }

    @Activate
    protected void activate(Map<String, Object> properties, ComponentContext cc) {
        keyStoreServiceRef.activate(cc);
        process(properties);
    }

    @Modified
    protected void modify(Map<String, Object> properties) {
        process(properties);
    }

    @Deactivate
    protected void deactivate(int reason, ComponentContext cc) {
        keyStoreServiceRef.deactivate(cc);
        consumerUtil = null;
    }

    /***********************************
     * End OSGi-related fields and methods
     ***********************************/

    private void process(Map<String, Object> props) {
        if (props == null || props.isEmpty()) {
            return;
        }
        id = JwtUtils.trimIt((String) props.get(JwtUtils.CFG_KEY_ID));
        issuer = JwtUtils.trimIt((String) props.get(JwtUtils.CFG_KEY_ISSUER));
        sharedKey = JwtConfigUtil.processProtectedString(props, JwtUtils.CFG_KEY_SHARED_KEY);
        audiences = JwtUtils.trimIt((String[]) props.get(JwtUtils.CFG_KEY_AUDIENCES));
        sigAlg = JwtConfigUtil.getSignatureAlgorithm(getId(), props, JwtUtils.CFG_KEY_SIGNATURE_ALGORITHM);
        trustStoreRef = JwtUtils.trimIt((String) props.get(JwtUtils.CFG_KEY_TRUSTSTORE_REF));
        trustedAlias = JwtUtils.trimIt((String) props.get(JwtUtils.CFG_KEY_TRUSTED_ALIAS));
        clockSkewMilliSeconds = (Long) props.get(JwtUtils.CFG_KEY_CLOCK_SKEW);
        validationRequired = (Boolean) props.get(JwtUtils.CFG_KEY_VALIDATION_REQUIRED); // internal
        jwkEnabled = (Boolean) props.get(JwtUtils.CFG_KEY_JWK_ENABLED); // internal
        jwkEndpointUrl = JwtUtils.trimIt((String) props.get(JwtUtils.CFG_KEY_JWK_ENDPOINT_URL)); // internal
        sslRef = JwtUtils.trimIt((String) props.get(JwtUtils.CFG_KEY_SSL_REF));
        useSystemPropertiesForHttpClientConnections = (Boolean) props
                .get(JwtUtils.CFG_KEY_USE_SYSPROPS_FOR_HTTPCLIENT_CONNECTONS);
        amrClaim = JwtUtils.trimIt((String[]) props.get(JwtUtils.CFG_AMR_CLAIM));
        keyManagementKeyAlias = JwtUtils.trimIt((String) props.get(JwtUtils.CFG_KEY_KEY_MANAGEMENT_KEY_ALIAS));

        consumerUtil = new ConsumerUtils(keyStoreServiceRef);
        jwkSet = null; // the jwkEndpoint may have been changed during dynamic
                       // update
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getIssuer() {
        return issuer;
    }

    @Override
    @Sensitive
    public String getSharedKey() {
        return sharedKey;
    }

    @Override
    public List<String> getAudiences() {
        return audiences;
    }

    @Override
    public boolean ignoreAudClaimIfNotConfigured() {
        return false;
    }

    @Override
    public String getSignatureAlgorithm() {
        return sigAlg;
    }

    @Override
    public String getTrustStoreRef() {
        return trustStoreRef;
    }

    @Override
    public String getKeyStoreRef() {
        String keyStoreName = null;
        String sslRef = getSslRef();
        if (sslRef == null) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "sslRef not configured, so will use server-wide keystore");
            }
            return null;
        }
        Properties sslConfigProps = getSslConfigProperties(sslRef);
        if (sslConfigProps != null) {
            keyStoreName = sslConfigProps.getProperty(com.ibm.websphere.ssl.Constants.SSLPROP_KEY_STORE_NAME);
        }
        return keyStoreName;
    }

    @FFDCIgnore(Exception.class)
    Properties getSslConfigProperties(String sslRef) {
        SSLSupport sslSupportService = JwtUtils.getSSLSupportService();
        if (sslSupportService == null) {
            return null;
        }
        Properties sslConfigProps;
        try {
            sslConfigProps = (Properties) AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    return sslSupportService.getJSSEHelper().getProperties(sslRef);

                }
            });
        } catch (Exception e) {
            if (tc.isDebugEnabled()) {
                Tr.debug(tc, "Caught exception getting SSL properties: " + e);
            }
            return null;
        }
        return sslConfigProps;
    }

    @Override
    public String getTrustedAlias() {
        return trustedAlias;
    }

    @Override
    public long getClockSkew() {
        return clockSkewMilliSeconds;
    }

    @Override
    public long getTokenAge() {
        return 0;
    }

    @Override
    public boolean getJwkEnabled() {
        return jwkEnabled;
    }

    @Override
    public String getJwkEndpointUrl() {
        return jwkEndpointUrl;
    }

    @Override
    public ConsumerUtils getConsumerUtils() {
        return consumerUtil;
    }

    @Override
    public boolean isValidationRequired() {
        return validationRequired;
    }

    @Override
    public String getSslRef() {
        return sslRef;
    }

    // 241154
    @Override
    public boolean isHostNameVerificationEnabled() {
        return true;
    }

    @Override
    public JWKSet getJwkSet() {
        if (jwkSet == null) {
            jwkSet = new JWKSet();
        }
        return jwkSet;
    }

    @Override
    public boolean getTokenReuse() {
        // The common JWT code is not allowed to reuse JWTs. This could be
        // revisited later as a potential config option.
        return false;
    }

    @Override
    public boolean getUseSystemPropertiesForHttpClientConnections() {
        return useSystemPropertiesForHttpClientConnections;
    }

    @Override
    public List<String> getAMRClaim() {
        return amrClaim;
    }

    @Override
    public String getKeyManagementKeyAlias() {
        return keyManagementKeyAlias;
    }

    @Override
    public Key getJweDecryptionKey() throws GeneralSecurityException {
        String keyAlias = getKeyManagementKeyAlias();
        if (keyAlias != null) {
            String keyStoreRef = getKeyStoreRef();
            return JwtUtils.getPrivateKey(keyAlias, keyStoreRef);
        }
        return null;
    }

}
