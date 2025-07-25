package org.wso2.sample.identity.oauth2.grant.statickey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

public class StaticKeyGrant extends AbstractAuthorizationGrantHandler {

    private static final Log log = LogFactory.getLog(StaticKeyGrant.class);
    public static final String CUSTOM_GRANT_TYPE_IDENTIFIER = "static_key";
    private static final String VALID_STATIC_KEY = "my_json_secret_key";

    @Override
    public void init() throws IdentityOAuth2Exception {
        super.init();
        log.info("StaticKeyGrant initialized.");
    }

    @Override
    public boolean validateGrant(OAuthTokenReqMessageContext context) throws IdentityOAuth2Exception {
        log.info("Static Key Grant Handler hit");

        RequestParameter[] parameters = context.getOauth2AccessTokenReqDTO().getRequestParameters();

        if (parameters == null || parameters.length == 0) {
            String msg = "No request parameters found. Expected JSON body in 'data' parameter.";
            log.error(msg);
            throw new IdentityOAuth2Exception(msg);
        }

        String dataJson = null;
        for (RequestParameter param : parameters) {
            if ("data".equals(param.getKey()) && param.getValue() != null && param.getValue().length == 1) {
                dataJson = param.getValue()[0];
                log.debug("Found 'data' parameter in request: " + dataJson);
                break;
            }
        }

        if (dataJson == null || dataJson.trim().isEmpty()) {
            String msg = "'data' JSON parameter is missing or empty.";
            log.error(msg);
            throw new IdentityOAuth2Exception(msg);
        }

        ObjectMapper mapper = new ObjectMapper();
        StaticKeyGrantRequest grantRequest;
        try {
            log.debug("Trying to convert JSON to Java object: " + dataJson);
            grantRequest = mapper.readValue(dataJson, StaticKeyGrantRequest.class);
            log.debug("JSON successfully converted. AuthCode: " + grantRequest.getAuthCode() + ", Username: " + grantRequest.getUsername());
        } catch (Exception e) {
            String msg = "Failed to understand the JSON you sent: " + e.getMessage();
            log.error(msg, e);
            throw new IdentityOAuth2Exception(msg);
        }

        if (!VALID_STATIC_KEY.equals(grantRequest.getAuthCode())) {
            String msg = "Invalid static key.";
            log.warn(msg);
            throw new IdentityOAuth2Exception(msg);
        }

        String username = grantRequest.getUsername();
        if (username == null || username.trim().isEmpty()) {
            String msg = "Username missing in JSON.";
            log.error(msg);
            throw new IdentityOAuth2Exception(msg);
        }

        try {

            UserRealm userRealm = CarbonContext.getThreadLocalCarbonContext().getUserRealm();
            if (userRealm == null) {
                String msg = "UserRealm not available in CarbonContext.";
                log.error(msg);
                throw new AuthenticationFailedException(msg);
            }
            String tenantAwareUsername = MultitenantUtils.getTenantAwareUsername(username);

            boolean userExists = userRealm.getUserStoreManager().isExistingUser(tenantAwareUsername);
            if (!userExists) {
                String msg = "User '" + tenantAwareUsername + "' not found.";
                log.warn(msg);
                throw new IdentityOAuth2Exception(msg);
            }

            AuthenticatedUser authenticatedUser = AuthenticatedUser.createLocalAuthenticatedUserFromSubjectIdentifier(tenantAwareUsername);
            context.setAuthorizedUser(authenticatedUser);

            return true;

        } catch (UserStoreException e) {
            log.error("UserStoreException during user existence check.", e);
            throw new IdentityOAuth2Exception("Error accessing user store.", e);
        } catch (Exception e) {
            log.error("Unexpected error during user validation.", e);
            throw new IdentityOAuth2Exception("Unexpected user validation error.", e);
        }
    }

    @Override
    public boolean validateScope(OAuthTokenReqMessageContext tokReqCtx) {
        return true;
    }

    @Override
    public boolean isOfTypeApplicationUser() {
        return true;
    }

    @Override
    public boolean issueRefreshToken() {
        return true;
    }
}
