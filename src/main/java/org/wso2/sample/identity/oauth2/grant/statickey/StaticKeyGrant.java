package org.wso2.sample.identity.oauth2.grant.statickey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.wso2.carbon.context.CarbonContext;
import org.wso2.carbon.identity.application.authentication.framework.exception.AuthenticationFailedException;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.user.api.UserRealm;
import org.wso2.carbon.user.api.UserStoreException;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;
import org.json.JSONObject;
//import org.wso2.sample.identity.oauth2.grant.statickey.model.ExternalServiceResponse;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.*;

import java.io.IOException;

public class StaticKeyGrant extends AbstractAuthorizationGrantHandler {

    private static final Log log = LogFactory.getLog(StaticKeyGrant.class);
    public static final String CUSTOM_GRANT_TYPE_IDENTIFIER = "static_key";
    private static final String VALID_STATIC_KEY = "my_json_secret_key";
    static final String EXTERNAL_SERVICE_URL = "https://static-key.free.beeceptor.com";
    private static final long CACHE_EXPIRY_MS = 300000;
    private static final String CACHE_PREFIX = "STATIC_KEY_";

    @Override
    public boolean validateGrant(OAuthTokenReqMessageContext context) throws IdentityOAuth2Exception {
        try {
            StaticKeyGrantRequest grantRequest = parseAndValidateRequest(context);

            validateUserExists(grantRequest.getUsername());

            validateWithExternalService(grantRequest);

            setAuthenticatedUser(context, grantRequest.getUsername());

            return true;
        } catch (IdentityOAuth2Exception e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error: " + e.getMessage(), e);
            throw new IdentityOAuth2Exception("Validation error: " + e.getMessage());
        }
    }

    private StaticKeyGrantRequest parseAndValidateRequest(OAuthTokenReqMessageContext context)
            throws Exception {
        RequestParameter[] parameters = context.getOauth2AccessTokenReqDTO().getRequestParameters();
        if (parameters == null || parameters.length == 0) {
            throw new IdentityOAuth2Exception("No request parameters found");
        }

        String jsonData = null;
        for (RequestParameter param : parameters) {
            if ("data".equals(param.getKey()) && param.getValue() != null && param.getValue().length > 0) {
                jsonData = param.getValue()[0];
                break;
            }
        }

        if (jsonData == null) {
            throw new IdentityOAuth2Exception("Missing 'data' parameter");
        }

        StaticKeyGrantRequest grantRequest = new ObjectMapper().readValue(jsonData, StaticKeyGrantRequest.class);

        if (!VALID_STATIC_KEY.equals(grantRequest.getAuthCode())) {
            throw new IdentityOAuth2Exception("Invalid static key");
        }

        if (grantRequest.getUsername() == null || grantRequest.getUsername().trim().isEmpty()) {
            throw new IdentityOAuth2Exception("Username is required");
        }

        return grantRequest;
    }

    private void validateUserExists(String username) throws Exception {
        UserRealm userRealm = CarbonContext.getThreadLocalCarbonContext().getUserRealm();
        if (userRealm == null) {
            throw new AuthenticationFailedException("Unable to access user realm");
        }

        String tenantAwareUsername = MultitenantUtils.getTenantAwareUsername(username);
        if (!userRealm.getUserStoreManager().isExistingUser(tenantAwareUsername)) {
            throw new UserStoreException("User not found");
        }
    }

    private void validateWithExternalService(StaticKeyGrantRequest request) throws Exception {
        StaticKeyCacheKey cacheKey = new StaticKeyCacheKey(request.getAuthCode(), request.getUsername());
        StaticKeyCacheEntry cachedEntry = StaticKeyCache.getInstance().getFromCache(cacheKey);

        if (cachedEntry != null && "success".equalsIgnoreCase(cachedEntry.getResponse().optString("status"))) {
            return;
        }

        String requestJson = String.format(
                "{\"authCode\":\"%s\",\"username\":\"%s\"}",
                request.getAuthCode(),
                request.getUsername()
        );

        HttpCallTask httpTask = new HttpCallTask(requestJson, EXTERNAL_SERVICE_URL);
        //TODO: why use Future in here? what are the benefits?
        Future<?> future = HttpThreadPoolExecutor.submitTask(httpTask);

        try {
            future.get(5, TimeUnit.SECONDS);
            JSONObject jsonResponse = httpTask.getResponse();

            if ("success".equalsIgnoreCase(jsonResponse.optString("status"))) {
                StaticKeyCache.getInstance().addToCache(
                        cacheKey,
                        new StaticKeyCacheEntry(jsonResponse, CACHE_EXPIRY_MS)
                );
            } else {
                throw new IdentityOAuth2Exception("External validation failed");
            }
        } catch (TimeoutException e) {
            future.cancel(true);
            throw new IdentityOAuth2Exception("External service call timed out");
        } catch (ExecutionException e) {
            throw new IdentityOAuth2Exception("External service call failed: " + e.getCause().getMessage());
        }
    }





    private void setAuthenticatedUser(OAuthTokenReqMessageContext context, String username) {
        AuthenticatedUser authenticatedUser = AuthenticatedUser.createLocalAuthenticatedUserFromSubjectIdentifier(
                MultitenantUtils.getTenantAwareUsername(username));
        context.setAuthorizedUser(authenticatedUser);
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





