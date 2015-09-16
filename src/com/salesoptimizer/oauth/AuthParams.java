package com.salesoptimizer.oauth;

/**
 * POJO to handle parameters required to authorize request using OAuth
 */
public class AuthParams {
    
    public enum OrgType {PRODUCTION, SANDBOX}
    
    private String orgId = "";
    private Boolean sandbox = false;
    private String consumerKey = "";
    private String consumerSecret = "";
    private String redirectUri = "";
    
    public String getOrgId() {
        return orgId;
    }
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }
    public Boolean isSandbox() {
        return sandbox;
    }
    public void setSandbox(Boolean sandbox) {
        this.sandbox = sandbox;
    }
    public String getConsumerKey() {
        return consumerKey;
    }
    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }
    public String getConsumerSecret() {
        return consumerSecret;
    }
    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }
    public String getRedirectUri() {
        return redirectUri;
    }
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
    
}
