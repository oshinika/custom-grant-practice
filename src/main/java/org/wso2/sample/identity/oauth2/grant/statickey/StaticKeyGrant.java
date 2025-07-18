package org.wso2.sample.identity.oauth2.grant.statickey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;

public class StaticKeyGrant extends AbstractAuthorizationGrantHandler {


    private static final Log log = LogFactory.getLog(StaticKeyGrant.class);
    private static final String STATIC_KEY_PARAM = "static_key";
    private static final String VALID_STATIC_KEY = "my_practice_key";
    private static final String AUTH_USER_NAME = "admin";
    private static final String AUTH_TENET_DOMAIN = "carbon.super";


    @Override
    public void validateGrant()
}
