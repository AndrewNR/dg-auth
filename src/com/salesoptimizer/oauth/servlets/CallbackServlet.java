package com.salesoptimizer.oauth.servlets;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.salesoptimizer.oauth.AuthParams;
import com.salesoptimizer.oauth.AuthorizeManager;
import com.salesoptimizer.oauth.OAuthSettings;
import com.salesoptimizer.oauth.OAuthUtils;
import com.salesoptimizer.oauth.TempStorage;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;

@SuppressWarnings("serial")
public class CallbackServlet extends HttpServlet {

    protected static final Logger log = Logger.getLogger(CallbackServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        try {
            String oauthToken = (String) req.getParameter(OAuth.OAUTH_TOKEN);
            String oauthConsumerKey = (String) req.getParameter(OAuth.OAUTH_CONSUMER_KEY);
            String oauthVerifier = (String) req.getParameter(OAuth.OAUTH_VERIFIER);
            log.info("oauthToken=" + oauthToken);
            log.info("oauthConsumerKey=" + oauthConsumerKey);
            log.info("oauthVerifier=" + oauthVerifier);
            
            OAuthSettings settings = (OAuthSettings) TempStorage.getInstance().get(TempStorage.KEY_SETTINGS);
            AuthParams params = (AuthParams) TempStorage.getInstance().get(TempStorage.KEY_PARAMS);
            
            OAuthAccessor accessor = new OAuthAccessor(new OAuthConsumer(
                    params.getRedirectUri(), params.getConsumerKey(),
                    params.getConsumerSecret(), null));
            accessor.requestToken = oauthToken;
            accessor.tokenSecret = AuthorizeManager.getInstance().getAuthTokenSecret(oauthToken);
            log.info("accessor.requestToken=" + accessor.requestToken);
            
            String response = OAuthUtils.getAccessToken(accessor, oauthVerifier, settings.urlAccessToken);
            log.info("access token=" + accessor.accessToken);
            log.info("access token secret=" + accessor.tokenSecret);
            log.info("response=" + response);
            log.info("Logging into Salesforce.com since now have the access tokens");

            String loginResponse = OAuthUtils.getNewSfdcSession(accessor, settings.urlApiLogin);
            if (loginResponse.startsWith("<")) {
                OAuthUtils.XmlResponseHandler xmlHandler = OAuthUtils.parseResponse(loginResponse);
                String serviceEndpoint = xmlHandler.getServerUrl();
                String sessionId = xmlHandler.getSessionId();
                log.info("service endpoint=" + serviceEndpoint);
                log.info("sessionId=" + sessionId);
                
                // Store sessionId for appId
                AuthorizeManager.getInstance().setAuthToken(params.getOrgId(), sessionId);
            }
            resp.sendRedirect("/auth");
        } catch (Exception e) {
            log.info("Callback servlet exception=" + e.toString());
        }
    }
}
