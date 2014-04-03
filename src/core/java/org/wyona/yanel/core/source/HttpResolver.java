package org.wyona.yanel.core.source;

import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.params.BasicHttpParams;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.wyona.yanel.core.Resource;

/**
 * Resolves URIs with scheme "http" and "https".
 * 
 * Syntax:
 * http:{path}
 * 
 * e.g.
 * http://foo/bar.xml
 * 
 */
public class HttpResolver implements URIResolver {

    private static final Logger log = LogManager.getLogger(HttpResolver.class);

    private static final String HTTP_SCHEME = "http";
    private static final String HTTPS_SCHEME = "https";
    
    //private Resource resource;

    private int connectionTimeout = -1;
    private int socketTimeout = -1;

    /**
     * @param resource Resource associated with source resolving
     */
    public HttpResolver(Resource resource) {
        //this.resource = resource;
    }

    /**
     * @param resource Resource associated with source resolving
     * @param connectionTimeout Value of CONNECTION_TIMEOUT
     * @param socketTimeout Value of SO_TIMEOUT
     */
    public HttpResolver(Resource resource, int connectionTimeout, int socketTimeout) {
        //this.resource = resource;
        this.connectionTimeout = connectionTimeout;
        this.socketTimeout = socketTimeout;
    }

    /**
     * @see javax.xml.transform.URIResolver#resolve(String, String)
     */
    public Source resolve(String href, String base) throws SourceException {
        String httpPrefix = HTTP_SCHEME + ":";
        String httpsPrefix = HTTPS_SCHEME + ":";
        
        // INFO: Only accept URIs which start either with 'http:' or 'https:'
        if (href != null && (href.startsWith(httpPrefix) || href.startsWith(httpsPrefix))){
            log.debug("href '" + href + "' seems to start with the correct scheme.");
        } else {
            log.error("Href '" + href + "' does neither start with prefix '" + httpPrefix + "' nor with '" + httpsPrefix + "'!");
            return null;
        }

        try {
            java.net.URL url = new java.net.URL(href);
            if (log.isDebugEnabled()) log.debug("Resolve: " + url.toString());
            // TODO: Make BASIC AUTH username and password configurable (similar to connection- and socket-timeout)
            DefaultHttpClient httpClient = getHttpClient(url, null, null, connectionTimeout, socketTimeout);
            HttpGet httpGet = new HttpGet(url.toString());
            org.apache.http.HttpResponse response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                return new StreamSource(response.getEntity().getContent());
            } else {
                throw new Exception("Response code '" + response.getStatusLine().getStatusCode() + "'");
            }
        } catch (java.net.SocketTimeoutException e) {
            String errorMsg = "Socket timeout while requesting '" + href + "' (" + e.toString() + "), whereas socket timeout is set to '" + socketTimeout + "' (Hint: Maybe it makes sense to increase this value).";
            log.error(errorMsg, e);
            throw new SourceException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = "Could not resolve URI: " + href + ": " + e.toString();
            log.error(errorMsg, e);
            throw new SourceException(errorMsg, e);
        }
    }

    /**
     * Get http client using SSL if necessary and basic authentication set
     * @param username Username for basic authentication
     * @param password Password for basic authentication
     * @param connectionTimeout Value of CONNECTION_TIMEOUT
     * @param socketTimeout Value of SO_TIMEOUT
     */
    private DefaultHttpClient getHttpClient(URL url, String username, String password, int connectionTimeout, int socketTimeout) throws Exception {
        HttpParams httpParams = new BasicHttpParams();
        if (connectionTimeout >= 0) {
            HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
        } else {
            log.warn("No connection timeout set, hence use default value: " + HttpConnectionParams.getConnectionTimeout(httpParams));
        }
        if (socketTimeout >= 0) {
            HttpConnectionParams.setSoTimeout(httpParams, socketTimeout);
        } else {
            log.warn("No socket timeout set, hence use default value: " + HttpConnectionParams.getSoTimeout(httpParams));
        }
        String yanelVersion = org.wyona.yanel.core.Yanel.getInstance().getVersion();
        HttpProtocolParams.setUserAgent(httpParams, "Yanel/" + yanelVersion + " HttpResolver");
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

        // INFO: http://stackoverflow.com/questions/7201154/httpclient-1-4-2-explanation-needed-for-custom-ssl-context-example
        if (url.getProtocol().equals("https")) {
            httpClient.getConnectionManager().getSchemeRegistry().register(new org.apache.http.conn.scheme.Scheme("https", 443, getSSLFactory()));
        } else {
            log.warn("Unsecure connection: " + url);        }

        if (username != null && password != null) {
            log.debug("Set BASIC AUTH for username '" + username + "'...");
            httpClient.getCredentialsProvider().setCredentials(new org.apache.http.auth.AuthScope(url.getHost(), url.getPort()), new org.apache.http.
auth.UsernamePasswordCredentials(username, password));
        } else {
            log.debug("No BASIC AUTH credentials set.");
        }

        return httpClient;
    }

    /**
     * Get SSL factory
     */
    private org.apache.http.conn.ssl.SSLSocketFactory getSSLFactory() throws Exception {
        // TODO: Make SSLSocketFactory configurable...

        // INFO: Just trust the certificate without checking/comparing a list of trusted certificates
        org.apache.http.conn.ssl.SSLSocketFactory factory = new org.apache.http.conn.ssl.SSLSocketFactory(new org.apache.http.conn.ssl.TrustStrategy() {
            public boolean isTrusted(final java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                return true;
            }
        }, org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        //org.apache.http.conn.ssl.SSLSocketFactory factory = new org.apache.http.conn.ssl.SSLSocketFactory(getSSLContext(), new org.apache.http.conn.ssl.StrictHostnameVerifier());

        return factory;
    }
}
