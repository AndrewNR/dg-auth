package com.salesoptimizer.oauth;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

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



    static final SAXParserFactory PARSER;
    static final String KEY_SERVER_URL = "serverUrl";
    static final String KEY_METADATA_SERVER_URL = "metadataServerUrl";
    static final String KEY_SESSION_ID = "sessionId";
    static final String KEY_SANDBOX = "sandbox";
    static final String KEY_RESPONSE = "response";
    static final String KEY_MESSAGE = "message";
    static final String KEY_ERROR = "error";

    static {
        PARSER = SAXParserFactory.newInstance();
        PARSER.setNamespaceAware(false);
    }

    // parses xml returned in the resonse
    public static XmlResponseHandler parseResponse(String response) throws Exception {
        XmlResponseHandler handler = new XmlResponseHandler();
        PARSER.newSAXParser().parse(new ByteArrayInputStream(response.getBytes()), handler);
        return handler;
    }

    public static class XmlResponseHandler extends DefaultHandler {

        private Map<String, String> xmlContent = new HashMap<String, String>();
        private String thisXmlTag;

        @Override
        public void startElement(String namespaceURI, String localName, String qualifiedName, Attributes atts)
                throws SAXException {
            thisXmlTag = qualifiedName;
        }

        @Override
        public void endElement(String namespaceURI, String localName, String qualifiedName) throws SAXException {
            thisXmlTag = null;
        }

        @Override
        public void characters(char[] text, int start, int length) throws SAXException {
            if (thisXmlTag != null) {
                xmlContent.put(thisXmlTag, new String(text, start, length));
            }
        }

        /**
         * @return the associated session
         */
        public String getSessionId() {
            return xmlContent.get(KEY_SESSION_ID);
        }

        /**
         * @return the server url
         */
        public String getServerUrl() {
            return xmlContent.get(KEY_SERVER_URL);
        }

        /**
         * @return the url for the metadata
         */
        public String getMetadataServerUrl() {
            return xmlContent.get(KEY_METADATA_SERVER_URL);
        }

        /**
         * @return boolean if sandbox org
         */
        public boolean isSandbox() {
            return Boolean.valueOf(xmlContent.get(KEY_SANDBOX));
        }

        /**
         * @return the associated message
         */
        public String getMessage() {
            return xmlContent.get(KEY_MESSAGE);
        }

        /**
         * @return the associated error
         */
        public String getError() {
            return xmlContent.get(KEY_ERROR);
        }

    }
}
