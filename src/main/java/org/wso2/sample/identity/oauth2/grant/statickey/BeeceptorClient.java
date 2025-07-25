
package org.wso2.sample.identity.oauth2.grant.statickey;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;

public class BeeceptorClient {
    private static final String BEECEPTOR_ENDPOINT = "https://localhost:9443/oauth2/token";

    public static void main(String[] args) {

                StaticKeyGrantRequest requestBodyObject = new StaticKeyGrantRequest(
                "my_json_secret_key",
                "admin",
                "123456789",
                "OMNI_ABC_789",
                "STAN_XYZ_456",
                "94771234567",
                "OTP_LOGIN",
                "Mobile OTP Verified Login"
        );

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> outerJsonPayload = new java.util.HashMap<>();
        outerJsonPayload.put("grant_type", "static_key");

        outerJsonPayload.put("data", requestBodyObject);

        String finalJsonRequestBody;
        try {
            finalJsonRequestBody = objectMapper.writeValueAsString(outerJsonPayload);
            System.out.println(finalJsonRequestBody);

        } catch (Exception e) {
            System.err.println("Error constructing final JSON request: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        HttpURLConnection connection = null;

        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) { return true; }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            URL url = new URL(BEECEPTOR_ENDPOINT);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Basic T3BDeXlkTldtWVMwSXFGQlY4Y29qa0FJYmNNYTo5dlF2QV81Q09tRXhDdVF6NlZNVjNLOVhkWERIbzZVUkFpQ2dsR1Rqb25vYQ==");


            connection.setDoOutput(true);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = finalJsonRequestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("\nResponse Code: " + responseCode);

            BufferedReader in;
            if (responseCode >= 200 && responseCode < 300) {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
            }

            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            System.out.println("Response Body: " + response.toString());

            ObjectMapper responseMapper = new ObjectMapper();
            try {
                Map<String, Object> responseMap = responseMapper.readValue(response.toString(), Map.class);
                String accessToken = (String) responseMap.get("access_token");

                if (accessToken != null && !accessToken.isEmpty()) {
                    System.out.println(accessToken);
                } else {
                    System.out.println("\nAccess token not found in the response or was empty.");
                }

            } catch (Exception e) {
                System.err.println("Error parsing access token from response");
                e.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("HTTP request failed");
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
