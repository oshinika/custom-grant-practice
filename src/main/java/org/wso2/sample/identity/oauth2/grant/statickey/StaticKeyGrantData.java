package org.wso2.sample.identity.oauth2.grant.statickey;

import jakarta.validation.constraints.NotBlank;

public class StaticKeyGrantData {

    @NotBlank(message = "authCode cannot be blank")
    private String authCode;


    private String username;


    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}