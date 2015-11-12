package com.salesoptimizer.oauth;

import java.util.List;

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
        return CommonUtils.isNotBlank(getAuthToken(params));
    }
    
    public String getAuthToken(AuthParams params) {
        return params != null ? getAuthToken(params.getAuthTokenKey()) : null;
    }
    public void setAuthToken(AuthParams params, String sessionId) {
        if (params != null) {
            setAuthToken(params.getAuthTokenKey(), sessionId);
        }
    }
    private String getAuthToken(String key) {
        return AuthTokenStorage.getInstance().getToken(key);
    }
    private void setAuthToken(String orgId, String sessionId) {
        AuthTokenStorage.getInstance().setToken(orgId, sessionId);
    }

    public String getAuthTokenSecret(String tokenKey) {
        return AuthTokenStorage.getInstance().getTokenSecret(tokenKey);
    }
    public String setAuthTokenSecret(String tokenKey, String tokenSecret) {
        return AuthTokenStorage.getInstance().setTokenSecret(tokenKey, tokenSecret);
    }

    public List<String> listTokenKeys() {
        return AuthTokenStorage.getInstance().getTokenKeys();
    }
    
    public boolean deleteToken(String key) {
        return AuthTokenStorage.getInstance().deleteToken(key);
    }
    
    public boolean deleteAllTokens() {
        return AuthTokenStorage.getInstance().deleteAllTokens();
    }

}
