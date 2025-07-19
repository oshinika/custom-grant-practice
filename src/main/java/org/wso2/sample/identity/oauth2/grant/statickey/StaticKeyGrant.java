package org.wso2.sample.identity.oauth2.grant.statickey;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

public class StaticKeyGrant extends AbstractAuthorizationGrantHandler {

    public static final String CUSTOM_GRANT_TYPE_IDENTIFIER = "urn:example:params:oauth:grant-type:static_key";
    private static final Log log = LogFactory.getLog(StaticKeyGrant.class);
    private static final String STATIC_KEY_PARAM = "static_key";
    private static final String VALID_STATIC_KEY = "my_practice_key";
    private static final String AUTH_USER_NAME = "admin";
    private static final String AUTH_TENANT_DOMAIN = "carbon.super";


    @Override
    public boolean validateGrant(OAuthTokenReqMessageContext oAuthTokenReqMessageContext) throws IdentityOAuth2Exception{

        log.info("Static Key Grant Handler hit for validateGrant()");
        super.validateGrant(oAuthTokenReqMessageContext);

        RequestParameter[] parameters = oAuthTokenReqMessageContext.getOauth2AccessTokenReqDTO().getRequestParameters();

        String receivedStaticKey = null;

        for (RequestParameter parameter : parameters){
            if (STATIC_KEY_PARAM.equals(parameter.getKey())){
                // Ensure parameter value exists before accessing index 0
                if (parameter.getValue() != null && parameter.getValue().length > 0) {
                    receivedStaticKey = parameter.getValue()[0];
                }
                break;
            }
        }

        if (receivedStaticKey == null || receivedStaticKey.trim().isEmpty()){
            String errorMsg = "Required parameter is missing or empty.";
            log.error(errorMsg);
            throw new IdentityOAuth2Exception(errorMsg);
        }

        if (VALID_STATIC_KEY.equals(receivedStaticKey)){
            log.info("Static Key Validation Successful");


            AuthenticatedUser authenticatedUser = AuthenticatedUser.createLocalAuthenticatedUserFromSubjectIdentifier(
                    MultitenantUtils.getTenantAwareUsername(AUTH_USER_NAME + "@" + AUTH_TENANT_DOMAIN)
            );

            oAuthTokenReqMessageContext.setAuthorizedUser(authenticatedUser);
            oAuthTokenReqMessageContext.setScope(oAuthTokenReqMessageContext.getOauth2AccessTokenReqDTO().getScope());

            return true;
        }
        else {
            // If the static key is invalid, throw an IdentityOAuth2Exception with an INVALID_GRANT error code.
            // INVALID_GRANT is the appropriate error code when the provided credentials (static key in this case) are invalid.
            String errorMsg = "Invalid static_key provided.";
            log.warn("Authentication failed for static key. Reason: " + errorMsg);
            throw new IdentityOAuth2Exception(errorMsg);
        }
    }

    @Override
    public boolean validateScope(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {

        return true;
    }


    @Override
    public boolean isOfTypeApplicationUser() throws IdentityOAuth2Exception {

        return true;
    }


}



