//package org.wso2.sample.identity.oauth2.grant.statickey;
//
//import jakarta.validation.constraints.NotBlank;
//// import jakarta.validation.Valid; // No longer needed if 'data' is removed as a nested POJO
//
//public class StaticKeyGrantRequest { // This POJO now directly represents the JSON from your log
//
//    @NotBlank(message = "authCode cannot be blank")
//    private String authCode;
//
//    // No @NotBlank here, assume optional or separate validation if username can be empty
//    private String username;
//
//    // --- ADD ALL THESE FIELDS AS TOP-LEVEL ---
//    private String cifNo;
//    private String omniId;
//    private String stanId;
//    private String mobileNo;
//    private String authType;
//    private String authDescription;
//    // --- END ADDED FIELDS ---
//
//    // Note: You will NOT have 'grant_type', 'client_id', 'scope', or 'data' fields here anymore.
//
//    // --- Getters and Setters for all these fields ---
//    public String getAuthCode() { return authCode; }
//    public void setAuthCode(String authCode) { this.authCode = authCode; }
//
//    public String getUsername() { return username; }
//    public void setUsername(String username) { this.username = username; }
//
//    public String getCifNo() { return cifNo; }
//    public void setCifNo(String cifNo) { this.cifNo = cifNo; }
//
//    public String getOmniId() { return omniId; }
//    public void setOmniId(String omniId) { this.omniId = omniId; }
//
//    public String getStanId() { return stanId; }
//    public void setStanId(String stanId) { this.stanId = stanId; } // Corrected typo here, should be setStanId
//
//    public String getMobileNo() { return mobileNo; }
//    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }
//
//    public String getAuthType() { return authType; }
//    public void setAuthType(String authType) { this.authType = authType; }
//
//    public String getAuthDescription() { return authDescription; }
//    public void setAuthDescription(String authDescription) { this.authDescription = authDescription; }
//}



package org.wso2.sample.identity.oauth2.grant.statickey;

import jakarta.validation.constraints.NotBlank;
// No @Valid or @NotNull needed for StaticKeyGrantData as it's no longer a nested POJO here

public class StaticKeyGrantRequest { // This POJO now directly represents the JSON from your log

    // These fields are now top-level as per the received JSON
    @NotBlank(message = "authCode cannot be blank")
    private String authCode;

    @NotBlank(message = "username cannot be blank") // Added @NotBlank based on common requirement
    private String username;

    private String cifNo;           // No @NotBlank/NotNull if optional
    private String omniId;
    private String stanId;
    private String mobileNo;
    private String authType;
    private String authDescription;

    // IMPORTANT: The following fields are NOT in the JSON your handler receives at the top level.
    // private String grant_type;
    // private String client_id;
    // private String scope;
    // private StaticKeyGrantData data; // This nested object is no longer here.

    // --- Getters and Setters for all these fields ---
    public String getAuthCode() { return authCode; }
    public void setAuthCode(String authCode) { this.authCode = authCode; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getCifNo() { return cifNo; }
    public void setCifNo(String cifNo) { this.cifNo = cifNo; }

    public String getOmniId() { return omniId; }
    public void setOmniId(String omniId) { this.omniId = omniId; }

    public String getStanId() { return stanId; }
    public void setStanId(String stanId) { this.stanId = stanId; }

    public String getMobileNo() { return mobileNo; }
    public void setMobileNo(String mobileNo) { this.mobileNo = mobileNo; }

    public String getAuthType() { return authType; }
    public void setAuthType(String authType) { this.authType = authType; }

    public String getAuthDescription() { return authDescription; }
    public void setAuthDescription(String authDescription) { this.authDescription = authDescription; }

    // Optional: Add a toString method for better debugging
    @Override
    public String toString() {
        return "StaticKeyGrantRequest{" +
                "authCode='" + authCode + '\'' +
                ", username='" + username + '\'' +
                ", cifNo='" + cifNo + '\'' +
                ", omniId='" + omniId + '\'' +
                ", stanId='" + stanId + '\'' +
                ", mobileNo='" + mobileNo + '\'' +
                ", authType='" + authType + '\'' +
                ", authDescription='" + authDescription + '\'' +
                '}';
    }
}