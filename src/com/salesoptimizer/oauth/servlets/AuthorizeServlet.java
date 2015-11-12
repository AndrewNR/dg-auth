package com.salesoptimizer.oauth.servlets;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;

import com.salesoptimizer.oauth.AuthConstants;
import com.salesoptimizer.oauth.AuthParams;
import com.salesoptimizer.oauth.AuthorizeManager;
import com.salesoptimizer.oauth.CommonUtils;
import com.salesoptimizer.oauth.OAuthSettings;
import com.salesoptimizer.oauth.OAuthUtils;

@SuppressWarnings("serial")
public class AuthorizeServlet extends HttpServlet {

    protected static final Logger log = Logger.getLogger(AuthorizeServlet.class.getName());
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthParams params = (AuthParams) req.getSession().getAttribute(AuthConstants.SESSION_ATTR_PARAMS);
        if (params == null) {
            params = buildAuthParams(req.getParameterMap());
            req.getSession().setAttribute(AuthConstants.SESSION_ATTR_PARAMS, params);
        }
        AuthorizeManager authManager = AuthorizeManager.getInstance();
        if (authManager.isAuthorized(params)) {
            cleanupTempSessionValues(req, resp);
            respondAuthorized(req, resp, authManager.getAuthToken(params));
            return;
        } else {
            doAuthorize(authManager, params, req, resp);
        }
    }

    private void cleanupTempSessionValues(HttpServletRequest req, HttpServletResponse resp) {
        req.getSession().removeAttribute(AuthConstants.SESSION_ATTR_PARAMS);
        req.getSession().removeAttribute(AuthConstants.SESSION_ATTR_SETTINGS);
    }

    private void respondAuthorized(HttpServletRequest req, HttpServletResponse resp, String authToken) throws ServletException, IOException {
        req.setAttribute("authToken", authToken);
        req.getRequestDispatcher("/authSuccess.jsp").forward(req, resp);
    }

    private void doAuthorize(AuthorizeManager authManager, AuthParams params,
            HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        OAuthSettings settings = (OAuthSettings) req.getSession().getAttribute(AuthConstants.SESSION_ATTR_SETTINGS);
        if (settings == null) {
            settings = new OAuthSettings(params.isSandbox());
            req.getSession().setAttribute(AuthConstants.SESSION_ATTR_SETTINGS, settings);
        }
        
        String oauthCallbackUrl = new StringBuilder("https://")
                .append(req.getServerName()).append(":").append(req.getLocalPort())
                .append(req.getContextPath()).append("/callback").toString();
        log.info("oauthCallbackUrl: 'https://{serverName}:{localPort}{contextPath}/callback' = " + oauthCallbackUrl);
        
        OAuthAccessor accessor = new OAuthAccessor(new OAuthConsumer(
                oauthCallbackUrl,
                params.getConsumerKey(),
                params.getConsumerSecret(),
                null));
        
        String response = OAuthUtils.getRequestToken(accessor, settings.urlRequestToken, oauthCallbackUrl);
        log.info("Request token=" + accessor.requestToken);
        log.info("Request token secret=" + accessor.tokenSecret);
        log.info("Response=" + response);
        
        if (response.startsWith("<") ||
                CommonUtils.isBlank(accessor.requestToken) ||
                CommonUtils.isBlank(accessor.tokenSecret)) {
            log.warning("Failed to get request token.");
            
            cleanupTempSessionValues(req, resp);
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            req.setAttribute(AuthConstants.PARAM_ERROR_MSG, "Request token failure: " + response);
            req.getRequestDispatcher("/error.jsp").forward(req, resp);
            return;
        }
        
        try {
            AuthorizeManager.getInstance().setAuthTokenSecret(accessor.requestToken, accessor.tokenSecret);

            String authUrl = OAuthUtils.buildAuthorizationUrl(accessor, settings.urlAuthorization);
            log.info("Authorization URL=" + authUrl);
            resp.sendRedirect(authUrl);
        } catch (Exception e) {
            log.severe("Exception=" + e.toString());
        }
    }

    private static AuthParams buildAuthParams(Map<String, String[]> requestParams) {
        AuthParams params = new AuthParams();
        params.setOrgId(getParamWithDefault(requestParams, AuthConstants.PARAM_ORG_ID));
        params.setSandbox(AuthConstants.ORG_TYPE_SANDBOX.equalsIgnoreCase( getParamWithDefault(requestParams, AuthConstants.PARAM_ORG_TYPE) ));
        params.setConsumerKey(getParamWithDefault(requestParams, AuthConstants.PARAM_CONSUMER_KEY));
        params.setConsumerSecret(getParamWithDefault(requestParams, AuthConstants.PARAM_CONSUMER_SECRET));
        params.setRedirectUri(getParamWithDefault(requestParams, AuthConstants.PARAM_REDIRECT_URI));
        return params;
    }

    private static String getParamWithDefault(Map<String, String[]> params, String key) {
        return getParamWithDefault(params, key, "");
    }

    private static String getParamWithDefault(Map<String, String[]> params, String key, String defaultValue) {
        String result = getParamValue(params, key);
        return CommonUtils.isNotEmpty(result) ? result : defaultValue;
    }

    private static String getParamValue(Map<String, String[]> params, String key) {
        String result = null;
        if (params != null && key != null) {
            String[] paramValues = params.get(key);
            if (paramValues != null && paramValues.length > 0) {
                result = paramValues[0];
            }
        }
        return result;
    }

}
