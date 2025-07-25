package org.wso2.sample.identity.oauth2.grant.statickey;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;

public class StaticKeyTokenClient extends AbstractTokenClient {

    private static final String DEFAULT_TOKEN_ENDPOINT = "https://localhost:9443/oauth2/token";

    private final StaticKeyGrantRequest grantRequestData;
    private final String authorizationHeader;
    public StaticKeyTokenClient(StaticKeyGrantRequest grantRequestData, String authorizationHeader) {
        super(DEFAULT_TOKEN_ENDPOINT);
        this.grantRequestData = grantRequestData;
        this.authorizationHeader = authorizationHeader;
    }
    public StaticKeyTokenClient(String tokenEndpoint, StaticKeyGrantRequest grantRequestData, String authorizationHeader) {
        super(tokenEndpoint);
        this.grantRequestData = grantRequestData;
        this.authorizationHeader = authorizationHeader;
    }
    @Override
    public String obtainAccessToken() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> outerJsonPayload = new java.util.HashMap<>();
        outerJsonPayload.put("grant_type", "static_key");
        outerJsonPayload.put("data", grantRequestData);

        String finalJsonRequestBody;
        try {
            finalJsonRequestBody = objectMapper.writeValueAsString(outerJsonPayload);
            System.out.println("Request Body for Static Key Grant: " + finalJsonRequestBody);
        } catch (Exception e) {
            throw new IOException("Error constructing final JSON request for StaticKeyGrant: " + e.getMessage(), e);
        }

        Map<String, Object> responseMap = makeTokenRequest(finalJsonRequestBody, authorizationHeader);
        String accessToken = (String) responseMap.get("access_token");

        if (accessToken != null && !accessToken.isEmpty()) {
            System.out.println("Access Token obtained: " + accessToken);
            return accessToken;
        } else {
            System.out.println("\nAccess token not found in the response or was empty. Full response: " + responseMap);
            return null;
        }
    }
}