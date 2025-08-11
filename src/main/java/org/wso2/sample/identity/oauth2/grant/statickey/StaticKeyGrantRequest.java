package org.wso2.sample.identity.oauth2.grant.statickey;
import java.io.Serializable;
//TODO: Why implement Serializable interface
public class StaticKeyGrantRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String authCode;
    private String username;
    private String cifNo;
    private String omniId;
    private String stanId;
    private String mobileNo;
    private String authType;
    private String authDescription;


    public StaticKeyGrantRequest(String authCode, String username, String cifNo, String omniId,
                                 String stanId, String mobileNo, String authType, String authDescription) {
        this.authCode = authCode;
        this.username = username;
        this.cifNo = cifNo;
        this.omniId = omniId;
        this.stanId = stanId;
        this.mobileNo = mobileNo;
        this.authType = authType;
        this.authDescription = authDescription;
    }

    public StaticKeyGrantRequest() {

    }


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

    public String getCifNo() {
        return cifNo;
    }

    public void setCifNo(String cifNo) {
        this.cifNo = cifNo;
    }

    public String getOmniId() {
        return omniId;
    }

    public void setOmniId(String omniId) {
        this.omniId = omniId;
    }

    public String getStanId() {
        return stanId;
    }

    public void setStanId(String stanId) {
        this.stanId = stanId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getAuthDescription() {
        return authDescription;
    }

    public void setAuthDescription(String authDescription) {
        this.authDescription = authDescription;
    }

}
