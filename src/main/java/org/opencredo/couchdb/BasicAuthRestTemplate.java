package org.opencredo.couchdb;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * A drop-in replacement for RestTemplate which <em>can</em> add basic auth credentials to the request: call
 * the constructor<b>with username and password</b> to perform basic auth</b> or <b>the default constructor
 * to create a {@link RestTemplate} without authentication</b>.
 * 
 * @author darabi@m-creations.net
 *
 */
public class BasicAuthRestTemplate extends RestTemplate {

    private CloseableHttpClient httpClient;

    public BasicAuthRestTemplate() {
        this(100);
    }

    public BasicAuthRestTemplate(int maxConnections) {
        super();
        PoolingHttpClientConnectionManager conMan = createConnectionManager(maxConnections);
        httpClient = HttpClients.custom().setConnectionManager(conMan).build();
        setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
    }

    public BasicAuthRestTemplate(String username, String password, String url) {
        this(username, password, url, 100);
    }

    public BasicAuthRestTemplate(String username, String password, String url, int maxConnections) {
        PoolingHttpClientConnectionManager conMan = createConnectionManager(maxConnections);
        CredentialsProvider provider = createCredentialsProvider(username, password, url);
        httpClient = HttpClients.custom().setConnectionManager(conMan).setDefaultCredentialsProvider(provider).build();
        createRequestFactory(url, httpClient);
    }

    public BasicAuthRestTemplate(String defaultDatabaseUrl) {
        String userInfo = null;
        try {
            URL url = new URL(defaultDatabaseUrl);
            userInfo = url.getUserInfo();
        } catch (MalformedURLException e) {
            new IllegalArgumentException("URL for CouchDB is malformed: " + defaultDatabaseUrl);
        }
        PoolingHttpClientConnectionManager conMan = createConnectionManager(100);
        HttpClientBuilder builder = HttpClients.custom().setConnectionManager(conMan);
        if (userInfo != null) {
            String[] creds = userInfo.split(":");
            if (creds.length < 2) {
                logger.warn("CouchDB URL contains a user name but no password");
            } else {
                CredentialsProvider provider = createCredentialsProvider(creds[0], creds[1], defaultDatabaseUrl);
                builder.setDefaultCredentialsProvider(provider);
                httpClient = builder.build();
                createRequestFactory(defaultDatabaseUrl, httpClient);
            }
        } else {
            httpClient = builder.build();
            setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        }
    }

    private void createRequestFactory(String url, HttpClient httpClient) {
        URL uri;
        try {
            uri = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL for CouchDB is malformed: " + url);
        }
        HttpHost host = createHttpHost(uri);
        setRequestFactory(new BasicAuthRequestFactory(httpClient, host));
    }

    private HttpHost createHttpHost(URL uri) {
        HttpHost host;
        int port = uri.getPort();
        if(port == -1) {
            String protocol = uri.getProtocol();
            if("http".equals(protocol))
                port = 80;
            else if("https".equals(protocol))
                port = 443;
            else
                throw new IllegalArgumentException("CouchDB URL has unknown protocol " + protocol);            
        }
        host = new HttpHost(uri.getHost(), port);
        return host;
    }

    private PoolingHttpClientConnectionManager createConnectionManager(int maxConnections) {
        PoolingHttpClientConnectionManager conMan = new PoolingHttpClientConnectionManager();
        conMan.setMaxTotal(maxConnections);
        return conMan;
    }

    CredentialsProvider createCredentialsProvider(String username, String password, String url) {
        URL targetUrl;
        try {
            targetUrl = new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL for CouchDB is malformed: " + url);
        }
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(targetUrl.getHost(), targetUrl.getPort()), new UsernamePasswordCredentials(username,
                password));
        return credsProvider;
    }

    /**
     * Create a new {@link ClientHttpRequest} via this template's {@link ClientHttpRequestFactory}.
     * 
     * @param url
     *            the URL to connect to
     * @param method
     *            the HTTP method to execute (GET, POST, etc.)
     * @return the created request
     * @throws IOException
     *             in case of I/O errors
     */
    @Override
    protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
        ClientHttpRequest request = getRequestFactory().createRequest(url, method);
        if (logger.isDebugEnabled()) {
            logger.debug("Created " + method.name() + " request for \"" + url.getScheme() + "://" + url.getHost() + ":" + url.getPort()
                    + url.getPath() + "\"");
        }
        return request;
    }
}