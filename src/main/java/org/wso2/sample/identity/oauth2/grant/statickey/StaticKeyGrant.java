//package org.wso2.sample.identity.oauth2.grant.statickey;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
//import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
//import org.wso2.carbon.identity.oauth2.model.RequestParameter;
//import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
//import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
//import org.wso2.carbon.utils.multitenancy.MultitenantUtils;
//
//public class StaticKeyGrant extends AbstractAuthorizationGrantHandler {
//
//    public static final String CUSTOM_GRANT_TYPE_IDENTIFIER = "grant-type:static_key";
//    private static final Log log = LogFactory.getLog(StaticKeyGrant.class);
//    public static final String STATIC_KEY_PARAM = "static_key";
//    private static final String VALID_STATIC_KEY = "my_practice_key";
//    private static final String AUTH_USER_NAME = "admin";
//    private static final String AUTH_TENANT_DOMAIN = "carbon.super";
//
//
//    @Override
//    public boolean validateGrant(OAuthTokenReqMessageContext oAuthTokenReqMessageContext) throws IdentityOAuth2Exception {
//        log.info("Static Key Grant Handler hit for validateGrant()");
//        super.validateGrant(oAuthTokenReqMessageContext);
//
//        RequestParameter[] parameters = oAuthTokenReqMessageContext.getOauth2AccessTokenReqDTO().getRequestParameters();
//
//        String receivedStaticKey = null;
//
//        for (RequestParameter parameter : parameters){
//            if (STATIC_KEY_PARAM.equals(parameter.getKey())){
//                // Ensure parameter value exists before accessing index 0
//                if (parameter.getValue() != null && parameter.getValue().length > 0) {
//                    receivedStaticKey = parameter.getValue()[0];
//                }
//                break;
//            }
//        }
//
//        if (receivedStaticKey == null || receivedStaticKey.trim().isEmpty()){
//            String errorMsg = "Required parameter is missing or empty.";
//            log.error(errorMsg);
//            throw new IdentityOAuth2Exception(errorMsg);
//        }
//
//        if (VALID_STATIC_KEY.equals(receivedStaticKey)){
//            log.info("Static Key Validation Successful");
//
//
//            AuthenticatedUser authenticatedUser = AuthenticatedUser.createLocalAuthenticatedUserFromSubjectIdentifier(
//                    MultitenantUtils.getTenantAwareUsername(AUTH_USER_NAME + "@" + AUTH_TENANT_DOMAIN)
//            );
//
//            oAuthTokenReqMessageContext.setAuthorizedUser(authenticatedUser);
//            oAuthTokenReqMessageContext.setScope(oAuthTokenReqMessageContext.getOauth2AccessTokenReqDTO().getScope());
//
//            return true;
//        }
//        else {
//
//            String errorMsg = "Invalid static_key provided.";
//            log.warn("Authentication failed for static key. Reason: " + errorMsg);
//            throw new IdentityOAuth2Exception(errorMsg);
//        }
//    }
//
//    @Override
//    public boolean validateScope(OAuthTokenReqMessageContext tokReqMsgCtx) throws IdentityOAuth2Exception {
//
//        return true;
//    }
//
//
//    @Override
//    public boolean isOfTypeApplicationUser() throws IdentityOAuth2Exception {
//
//        return true;
//    }
//
//
//    @Override
//    public boolean issueRefreshToken() throws IdentityOAuth2Exception {
//
//        return true;
//    }
//
//}






package org.wso2.sample.identity.oauth2.grant.statickey;
//
import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validation;
//import jakarta.validation.Validator;
//import jakarta.validation.ValidatorFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.oauth2.IdentityOAuth2Exception;
import org.wso2.carbon.identity.oauth2.dto.OAuth2AccessTokenReqDTO;
import org.wso2.carbon.identity.oauth2.model.RequestParameter;
import org.wso2.carbon.identity.oauth2.token.OAuthTokenReqMessageContext;
import org.wso2.carbon.identity.oauth2.token.handlers.grant.AbstractAuthorizationGrantHandler;
import org.wso2.carbon.utils.multitenancy.MultitenantUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

public class StaticKeyGrant extends AbstractAuthorizationGrantHandler {

    private static final Log log = LogFactory.getLog(StaticKeyGrant.class);
    public static final String CUSTOM_GRANT_TYPE_IDENTIFIER = "static_key";

    private static final String VALID_STATIC_KEY = "my_json_secret_key";
    private static final String AUTH_USER_NAME = "admin";
    private static final String AUTH_TENANT_DOMAIN = "carbon.super";

   // private Validator validator;

    @Override
    public void init() throws IdentityOAuth2Exception {
        super.init();

//        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
//            this.validator = factory.getValidator();
//        } catch (Exception e) {
//            String errorMsg = "Error initializing Hibernate Validator: " + e.getMessage();
//            log.error(errorMsg, e);
//            throw new IdentityOAuth2Exception(errorMsg, e);
//        }
        log.info("StaticKeyGrant initialized with Hibernate Validator.");
    }

    @Override
    public boolean validateGrant(OAuthTokenReqMessageContext oAuthTokenReqMessageContext) throws IdentityOAuth2Exception {

        log.info("Static Key Grant Handler hit ");
        RequestParameter[] parameters = oAuthTokenReqMessageContext.getOauth2AccessTokenReqDTO().getRequestParameters();
        /*String[] json = parameters[0].getValue();
        if (parameters == null) {
            String errorMsg = "HttpServletRequest is null. Cannot read JSON body. Ensure it's passed as a property.";
            log.error(errorMsg);
            throw new IdentityOAuth2Exception(errorMsg);
        }

        ObjectMapper mapper = new ObjectMapper();
        String jsonString;
        StaticKeyGrantRequest grantRequest; */


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
            grantRequest = mapper.readValue(jsonString, StaticKeyGrantRequest.class);
            log.debug("Successfully deserialized JSON " + grantRequest.toString());


        } catch (IOException e) {
            String errorMsg = "Error parsing JSON string to StaticKeyGrantRequest POJO: " + e.getMessage();
            log.error(errorMsg, e);
            throw new IdentityOAuth2Exception(errorMsg);
        }

//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
//            StringBuilder jsonBuilder = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                jsonBuilder.append(line);
//            }
//            jsonString = jsonBuilder.toString();
//
//            if (jsonString.trim().isEmpty()) {
//                String errorMsg = "JSON request body is empty.";
//                log.error(errorMsg);
//                throw new IdentityOAuth2Exception(errorMsg);
//            }
//
//
//            grantRequest = mapper.readValue(jsonString, StaticKeyGrantRequest.class);
//
//        } catch (IOException e) {
//            String errorMsg = "Error reading or parsing JSON request body: " + e.getMessage();
//            log.error(errorMsg, e);
//            throw new IdentityOAuth2Exception(errorMsg);
//        }

//        Set<ConstraintViolation<StaticKeyGrantRequest>> violations = validator.validate(grantRequest);
//
//        if (!violations.isEmpty()) {
//
//            String errorMsg = "Validation failed for Static Key Grant request: " +
//                    violations.stream()
//                            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
//                            .collect(Collectors.joining(", "));
//            log.error(errorMsg);
//            throw new IdentityOAuth2Exception(errorMsg);
//        }

//        String grantType = grantRequest.getGrant_type();
//        String clientId = grantRequest.getClient_id();
//        String scope = grantRequest.getScope();
//        String receivedAuthCode = grantRequest.getData().getAuthCode();
//        String receivedUsernameFromJSON = grantRequest.getData().getUsername();
//
//        OAuth2AccessTokenReqDTO tokenReqDTO = oAuthTokenReqMessageContext.getOauth2AccessTokenReqDTO();
//        tokenReqDTO.setGrantType(grantType);
//        tokenReqDTO.setClientId(clientId);
//        tokenReqDTO.setScope(new String[]{scope});
//
//        if (VALID_STATIC_KEY.equals(receivedAuthCode)) {
//            log.info("JSON Auth Code Validation Successful.");
//
//            String userToAuthenticate = (receivedUsernameFromJSON != null && !receivedUsernameFromJSON.trim().isEmpty()) ? receivedUsernameFromJSON : AUTH_USER_NAME;
//
//            AuthenticatedUser authenticatedUser = AuthenticatedUser.createLocalAuthenticatedUserFromSubjectIdentifier(
//                    MultitenantUtils.getTenantAwareUsername(userToAuthenticate + "@" + AUTH_TENANT_DOMAIN)
//            );
//            authenticatedUser.setTenantDomain(AUTH_TENANT_DOMAIN);
//
//            log.debug("Authenticated user details set for: " + authenticatedUser.getUserName());
//
//            oAuthTokenReqMessageContext.setAuthorizedUser(authenticatedUser);

            return true;
//        } else {
//            String errorMsg = "Invalid authentication code provided in JSON body.";
//            log.warn("Authentication failed for JSON auth code. Reason: " + errorMsg);
//            throw new IdentityOAuth2Exception(errorMsg);
//        }
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