package org.wso2.sample.identity.oauth2.grant.statickey;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        StaticKeyGrantRequest requestData = new StaticKeyGrantRequest(
                "my_json_secret_key",
                "admin",
                "123456789",
                "OMNI_ABC_789",
                "STAN_XYZ_456",
                "94771234567",
                "OTP_LOGIN",
                "Mobile OTP Verified Login"
        );

        String authorizationHeader = "Basic T3BDeXlkTldtWVMwSXFGQlY4Y29qa0FJYmNNYTo5dlF2QV81Q09tRXhDdVF6NlZNVjNLOVhkWERIbzZVUkFpQ2dsR1Rqb25vYQ==";
        StaticKeyTokenClient client = new StaticKeyTokenClient(requestData, authorizationHeader);

        try {
            String accessToken = client.obtainAccessToken();
            if (accessToken != null) {
                System.out.println("\nMain Application: Successfully received access token!");
            } else {
                System.out.println("\nMain Application: Failed to obtain access token.");
            }
        } catch (IOException e) {
            System.err.println("\nMain Application: An error occurred during token acquisition.");
            e.printStackTrace();
        }
    }
}