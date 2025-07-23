
package org.wso2.sample.identity.oauth2.grant.statickey;

import com.fasterxml.jackson.databind.ObjectMapper;
// import jakarta.validation.ConstraintViolation;
// import jakarta.validation.Validation;
// import jakarta.validation.Validator;
// import jakarta.validation.ValidatorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

// import javax.servlet.http.HttpServletRequest;
// import java.io.BufferedReader;
// import java.io.InputStreamReader;
import java.io.IOException; // Keep for ObjectMapper
// import java.util.Set;
// import java.util.stream.Collectors;

public class StaticKeyGrant extends AbstractAuthorizationGrantHandler {

    private static final Log log = LogFactory.getLog(StaticKeyGrant.class);
    public static final String CUSTOM_GRANT_TYPE_IDENTIFIER = "static_key";

    private static final String VALID_STATIC_KEY = "my_json_secret_key";
    private static final String AUTH_USER_NAME = "admin";
    private static final String AUTH_TENANT_DOMAIN = "carbon.super";

    // private Validator validator; // COMMENTED OUT

    @Override
    public void init() throws IdentityOAuth2Exception {
        super.init();
        log.info("StaticKeyGrant initialized (Hibernate Validator commented out)."); // Adjusted log message
    }

    @Override
    public boolean validateGrant(OAuthTokenReqMessageContext oAuthTokenReqMessageContext) throws IdentityOAuth2Exception {

        log.info("Static Key Grant Handler hit ");
        RequestParameter[] parameters = oAuthTokenReqMessageContext.getOauth2AccessTokenReqDTO().getRequestParameters();

        String jsonString = null;

        if (parameters == null || parameters.length == 0) {
            String errorMsg = "No request parameters found. Expected JSON in the request body.";
            log.error(errorMsg);
            throw new IdentityOAuth2Exception(errorMsg);
        }

        for (RequestParameter param : parameters) {
            if (param.getValue() != null && param.getValue().length == 1) {
                String potentialJsonString = param.getValue()[0];

                if (potentialJsonString != null &&
                        potentialJsonString.trim().startsWith("{") &&
                        potentialJsonString.trim().endsWith("}")) {

                    jsonString = potentialJsonString;
                    log.debug("Found JSON string from RequestParameters (heuristic check successful): " + jsonString);
                    break;
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("Found other parameter - Value length: " + (param.getValue() != null ? param.getValue().length : "null") +
                        ", First Value: " + (param.getValue() != null && param.getValue().length > 0 ? param.getValue()[0] : "N/A"));
            }
        }

        if (jsonString == null || jsonString.trim().isEmpty()) {
            String errorMsg = "JSON request body not found or is empty among request parameters. Ensure Content-Type is application/json and body is a valid JSON object.";
            log.error(errorMsg);
            throw new IdentityOAuth2Exception(errorMsg);
        }

        ObjectMapper mapper = new ObjectMapper();
        StaticKeyGrantRequest grantRequest;

        try {
            log.debug("DEBUGGING: Attempting to deserialize JSON string (length " + jsonString.length() + "): \"" + jsonString + "\"");
            grantRequest = mapper.readValue(jsonString, StaticKeyGrantRequest.class);
            log.debug("Successfully deserialized JSON to POJO: " + grantRequest.toString()); // Log for verification

        } catch (IOException e) {
            String errorMsg = "Error parsing JSON string to StaticKeyGrantRequest POJO: " + e.getMessage();
            log.error(errorMsg, e);
            throw new IdentityOAuth2Exception(errorMsg);
        }

        String receivedAuthCode = grantRequest.getAuthCode();
        String receivedUsernameFromJSON = grantRequest.getUsername();
        String cifNo = grantRequest.getCifNo(); // Example of accessing another field

        OAuth2AccessTokenReqDTO tokenReqDTO = oAuthTokenReqMessageContext.getOauth2AccessTokenReqDTO();

        String grantType = tokenReqDTO.getGrantType();
        if (grantType == null || !CUSTOM_GRANT_TYPE_IDENTIFIER.equals(grantType)) {
            log.warn("Grant type in DTO was not '" + CUSTOM_GRANT_TYPE_IDENTIFIER + "', using default.");
            grantType = CUSTOM_GRANT_TYPE_IDENTIFIER;
        }

        String clientId = tokenReqDTO.getClientId();
        if (clientId == null || clientId.trim().isEmpty()) {
            String errorMsg = "Client ID is missing in the OAuth2AccessTokenReqDTO. It must be provided via Basic Auth or form parameters.";
            log.error(errorMsg);
            throw new IdentityOAuth2Exception(errorMsg);
        }

        String[] scopes = tokenReqDTO.getScope();
        String scope = (scopes != null && scopes.length > 0) ? scopes[0] : null;
        if (scope == null || scope.trim().isEmpty()) {
            log.warn("Scope is missing from the OAuth2AccessTokenReqDTO. Using 'openid' as fallback.");
            scope = "openid"; // Default if no scope is given via standard OAuth param
        }

        // Set these (potentially adjusted) values back into the DTO for WSO2's core.
        tokenReqDTO.setGrantType(grantType);
        tokenReqDTO.setClientId(clientId);
        tokenReqDTO.setScope(new String[]{scope});

        if (VALID_STATIC_KEY.equals(receivedAuthCode)) {
            log.info("JSON Auth Code Validation Successful. Authenticating user.");
            log.debug("Authenticated CIF No: " + cifNo);

            String userToAuthenticate = (receivedUsernameFromJSON != null && !receivedUsernameFromJSON.trim().isEmpty()) ? receivedUsernameFromJSON : AUTH_USER_NAME;

            AuthenticatedUser authenticatedUser = AuthenticatedUser.createLocalAuthenticatedUserFromSubjectIdentifier(
                    MultitenantUtils.getTenantAwareUsername(userToAuthenticate + "@" + AUTH_TENANT_DOMAIN)
            );
            authenticatedUser.setTenantDomain(AUTH_TENANT_DOMAIN);


            log.debug("Authenticated user details set for: " + authenticatedUser.getUserName() + " in tenant " + authenticatedUser.getTenantDomain());

            oAuthTokenReqMessageContext.setAuthorizedUser(authenticatedUser);

            return true;
        } else {

            String errorMsg = "Invalid authentication code provided in JSON body.";
            log.warn("Authentication failed for static key. Reason: " + errorMsg);
            throw new IdentityOAuth2Exception(errorMsg); // Throw exception to stop token issuance
        }
    }

    @Override
    public boolean validateScope(OAuthTokenReqMessageContext tokReqCtx) throws IdentityOAuth2Exception {

        return true;
    }

    @Override
    public boolean isOfTypeApplicationUser() throws IdentityOAuth2Exception {
        return true;
    }

    @Override
    public boolean issueRefreshToken() throws IdentityOAuth2Exception {
        return true;
    }
}
