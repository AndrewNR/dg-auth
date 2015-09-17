package com.salesoptimizer.oauth;

public class AuthorizeManager {
    
    private static AuthorizeManager instance;

    private AuthorizeManager() {
    }

    public static AuthorizeManager getInstance() {
        if (instance == null) {
            instance = new AuthorizeManager();
        }
        return instance;
    }
    
    public boolean isAuthorized(AuthParams params) {
        return params != null && getAuthToken(params.getOrgId()) != null;
    }

    public String getAuthToken(AuthParams params) {
        return params != null ? getAuthToken(params.getOrgId()) : "";
    }
    public String getAuthToken(String key) {
        return AuthTokenStorage.getInstance().getToken(key);
    }
    public void setAuthToken(String orgId, String sessionId) {
        AuthTokenStorage.getInstance().setToken(orgId, sessionId);
    }

    public String getAuthTokenSecret(String tokenKey) {
        return AuthTokenStorage.getInstance().getTokenSecret(tokenKey);
    }
    public String setAuthTokenSecret(String tokenKey, String tokenSecret) {
        return AuthTokenStorage.getInstance().setTokenSecret(tokenKey, tokenSecret);
    }

}
