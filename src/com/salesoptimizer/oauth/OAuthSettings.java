package com.salesoptimizer.oauth;

public class OAuthSettings {

    public final String host;
    public final String urlRequestToken;
    public final String urlAccessToken;
    public final String urlAuthorization;
    
    // TODO: Not sure if we need these two... Just moving everything from another project
    public final String urlApiLogin;
    public final String urlAuthEndpoint;
    
    public OAuthSettings(boolean isSandbox) {
        host = isSandbox ? "https://test.salesforce.com" : "https://login.salesforce.com";
        urlRequestToken = host + "/_nc_external/system/security/oauth/RequestTokenHandler";
        urlAccessToken = host + "/_nc_external/system/security/oauth/AccessTokenHandler";
        urlAuthorization = host + "/setup/secur/RemoteAccessAuthorizationPage.apexp";
        
        urlApiLogin = host + "/services/OAuth/c/27.0";
        urlAuthEndpoint = host + "/services/Soap/u/27.0";
    }
}
