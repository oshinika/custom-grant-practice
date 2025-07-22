package org.wso2.sample.identity.oauth2.grant.statickey;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StaticKeyGrantRequest {

    @NotBlank(message = "grant_type cannot be blank")
    private String grant_type;

    @NotBlank(message = "client_id cannot be blank")
    private String client_id;

    @NotBlank(message = "scope cannot be blank")
    private String scope;

    @Valid
    @NotNull(message = "data object cannot be null")
    private StaticKeyGrantData data;



    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public StaticKeyGrantData getData() {
        return data;
    }

    public void setData(StaticKeyGrantData data) {
        this.data = data;
    }
}