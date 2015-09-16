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

    
}
