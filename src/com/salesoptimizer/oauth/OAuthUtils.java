package com.salesoptimizer.oauth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;

public class OAuthUtils {

    protected static final Logger log = Logger.getLogger(OAuthUtils.class.getName());

    // returns the response with the initial request token
    public static String getRequestToken(OAuthAccessor accessor, String requestTokenUrl, String callbackUrl) {
        log.info("Fetching request tokens..");
        Map<String, String> params = new HashMap<String, String>();
        params.put(OAuth.OAUTH_CALLBACK, callbackUrl);
        try {
            String response = doOauthGetRequest(accessor, requestTokenUrl, params);
            if (!response.startsWith("<")) {
                Map<String, String> responseParams = parseResponseParams(response);
                accessor.requestToken = responseParams.get(OAuth.OAUTH_TOKEN);
                accessor.tokenSecret = responseParams.get(OAuth.OAUTH_TOKEN_SECRET);
            }
            return response;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // returns the response with the access token
    public static String getAccessToken(OAuthAccessor accessor, String verifier, String accessTokenUrl) throws Exception {
        log.info("fetching access token...");
        Map<String, String> params = new HashMap<String, String>();
        params.put(OAuth.OAUTH_VERIFIER, verifier);
        params.put(OAuth.OAUTH_TOKEN, accessor.requestToken);
        try {
            String response = doOauthGetRequest(accessor, accessTokenUrl, params);
            if (!response.startsWith("<")) {
                Map<String, String> responseParams = parseResponseParams(response);
                accessor.accessToken = responseParams.get(OAuth.OAUTH_TOKEN);
                accessor.tokenSecret = responseParams.get(OAuth.OAUTH_TOKEN_SECRET);
            }
            return response;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // builds url for the authorization request
    public static String buildAuthorizationUrl(OAuthAccessor accessor, String authPage) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(authPage)
            .append("?")
            .append(OAuth.OAUTH_CONSUMER_KEY)
            .append("=")
            .append(URLEncoder.encode(accessor.consumer.consumerKey, "UTF-8"))
            .append("&")
            .append(OAuth.OAUTH_TOKEN)
            .append("=")
            .append(URLEncoder.encode(accessor.requestToken, "UTF-8"));
        return sb.toString();

    }

    // requests a new session from salesforce using the login url
    public static String getNewSfdcSession(OAuthAccessor accessor, String loginUrl) {
        Map<String, String> parameters = new HashMap<String, String>();
        try {
            OAuthMessage oauthMsg = accessor.newRequestMessage("POST", loginUrl, parameters.entrySet());
            String urlParameters = OAuth.addParameters(oauthMsg.URL, oauthMsg.getParameters());

            log.info("POST session request to=" + loginUrl);
            URL endpoint = new URL(loginUrl);
            HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String params = urlParameters.split("\\?")[1];
            log.info("Using OAuth parameters=" + params);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(params);
            writer.close();

            log.info("Response code=" + connection.getResponseCode());
            return readInputStream(connection.getInputStream());
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    // performs a GET request for tokens
    private static String doOauthGetRequest(OAuthAccessor accessor, String loginUrl, Map<String, String> parameters)
            throws Exception {
        OAuthMessage oauthMsg = accessor.newRequestMessage("GET", loginUrl, parameters.entrySet());
        String urlParameters = OAuth.addParameters(oauthMsg.URL, oauthMsg.getParameters());

        log.info("GET request to=" + urlParameters);
        URL endpoint = new URL(urlParameters);
        HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        log.info("Response code=" + connection.getResponseCode());
        return readInputStream(connection.getInputStream());
    }

    // reads the input stream
    private static String readInputStream(InputStream input) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        try {
            String line = reader.readLine();
            while (line != null) {
                sb.append(line + "\n");
                line = reader.readLine();
            }
        } catch (IOException e) {
            log.severe("Error reading input stream=" + e.toString());
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                log.severe("Error reading input stream=" + e.toString());
            }
        }
        return sb.toString().trim();
    }

    // parses the params from the response
    private static HashMap<String, String> parseResponseParams(String body) throws Exception {
        HashMap<String, String> results = new HashMap<String, String>();
        if (body != null && !body.isEmpty()) {
            for (String keyValuePair : body.split("&")) {
                String[] kvp = keyValuePair.split("=");
                if (kvp != null && kvp.length == 2) {
                    results.put(kvp[0], URLDecoder.decode(kvp[1], "UTF-8"));
                }
            }
        }
        return results;
    }

}
