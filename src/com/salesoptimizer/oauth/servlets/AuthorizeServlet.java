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

import com.salesoptimizer.oauth.AuthParams;
import com.salesoptimizer.oauth.AuthorizeManager;
import com.salesoptimizer.oauth.OAuthSettings;
import com.salesoptimizer.oauth.OAuthUtils;

@SuppressWarnings("serial")
public class AuthorizeServlet extends HttpServlet {

    protected static final Logger log = Logger.getLogger(AuthorizeServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AuthParams params = buildAuthParams(req.getParameterMap());
        AuthorizeManager authManager = AuthorizeManager.getInstance();
        if (authManager.isAuthorized(params)) {
            respondAuthorized(req, resp, authManager.getAuthToken(params));
            return;
        } else {
            doAuthorize(authManager, params, req, resp);
        }
    }

    private void respondAuthorized(HttpServletRequest req, HttpServletResponse resp, String authToken) throws ServletException, IOException {
        req.setAttribute("authToken", authToken);
        req.getRequestDispatcher("/authSuccess.jsp").forward(req, resp);
    }

    private void doAuthorize(AuthorizeManager authManager, AuthParams params, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String oauthCallbackUrl = new StringBuilder("https://").append(req.getServerName())
                .append(req.getContextPath())
                .append("/callback").toString();
        log.info("oauthCallbackUrl: 'https://{serverName}{contextPath}/callback' = " + oauthCallbackUrl);
        
        OAuthAccessor accessor = new OAuthAccessor(new OAuthConsumer(
                oauthCallbackUrl,
                params.getConsumerKey(),
                params.getConsumerSecret(),
                null));
        
        OAuthSettings settings = new OAuthSettings(params.isSandbox());
        String response = OAuthUtils.getRequestToken(accessor, settings.urlRequestToken, oauthCallbackUrl);
        log.info("Request token=" + accessor.requestToken);
        log.info("Request token secret=" + accessor.tokenSecret);
        log.info("Response=" + response);
        
        if (response.startsWith("<")) {
            log.warning("Failed to get request token.");
            resp.setContentType("text/html; charset=UTF-8");
            resp.getWriter().println("Request token failure!!");
            resp.getWriter().println(response);
            return;
        }
        
        // TODO: save tokenSecret somewhere; it will be needed in CallbackServlet!!!
        // OauthHelperUtils.REQUEST_TOKENS.put(accessor.requestToken, accessor.tokenSecret);
        
        try {
            String authUrl = OAuthUtils.buildAuthorizationUrl(accessor, settings.urlAuthorization);
            log.info("Authorization URL=" + authUrl);
            resp.sendRedirect(authUrl);
        } catch (Exception e) {
            log.severe("Exception=" + e.toString());
        }
    }

    private static AuthParams buildAuthParams(Map<String, String[]> requestParams) {
        AuthParams params = new AuthParams();
        params.setOrgId(getParamWithDefault(requestParams, "orgId"));
        params.setSandbox("sandbox".equalsIgnoreCase( getParamWithDefault(requestParams, "orgType") ));
        params.setConsumerKey(getParamWithDefault(requestParams, "consumerKey"));
        params.setConsumerSecret(getParamWithDefault(requestParams, "consumerSecret"));
        params.setRedirectUri(getParamWithDefault(requestParams, "redirectUri"));
        return params;
    }

    private static String getParamWithDefault(Map<String, String[]> params, String key) {
        return getParamWithDefault(params, key, "");
    }

    private static String getParamWithDefault(Map<String, String[]> params, String key, String defaultValue) {
        String result = getParamValue(params, key);
        return result != null ? result : defaultValue;
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
