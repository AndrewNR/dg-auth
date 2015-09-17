package com.salesoptimizer.oauth;

import java.util.HashMap;
import java.util.Map;

public class AuthTokenStorage {

    private static AuthTokenStorage instance;
    private static final Map<String, String> tokensMap = new HashMap<String, String>();
    private static final Map<String, String> tokenSecretsMap = new HashMap<String, String>();

    private AuthTokenStorage() {
    }

    static AuthTokenStorage getInstance() {
        if (instance == null) {
            instance = new AuthTokenStorage();
        }
        return instance;
    }

    String getToken(String key) {
        // TODO read from db
        return tokensMap.get(key);
    }

    void setToken(String key, String token) {
        // TODO write to db
        tokensMap.put(key, token);
    }

    String getTokenSecret(String authToken) {
        return tokenSecretsMap.get(authToken);
    }
    String setTokenSecret(String authToken, String authTokenSecret) {
        return tokenSecretsMap.get(authToken);
    }

}
