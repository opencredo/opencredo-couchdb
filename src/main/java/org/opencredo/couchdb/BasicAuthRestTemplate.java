package org.opencredo.couchdb;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * A drop-in replacement for RestTemplate which <em>can</em> add basic auth credentials to the request: 
 * call the constructor<b>with username and password</b> to perform basic auth</b> or <b>the default
 * constructor to create a {@link RestTemplate} without authentication</b>.
 * 
 * @author darabi@m-creations.net
 *
 */
@SuppressWarnings("deprecation")
public class BasicAuthRestTemplate extends RestTemplate {

   private HttpClient httpClient;

   public BasicAuthRestTemplate() {
        super();
        httpClient = new HttpClient();
        MultiThreadedHttpConnectionManager conMan = new MultiThreadedHttpConnectionManager();
        conMan.setMaxTotalConnections(50);
        conMan.setMaxConnectionsPerHost(50);
        httpClient.setHttpConnectionManager(conMan);
        setRequestFactory(new CommonsClientHttpRequestFactory(httpClient));
   }

   public BasicAuthRestTemplate(String username, String password) {
      this();
      setCredentials(httpClient, username, password);
      setRequestFactory(new CommonsClientHttpRequestFactory(httpClient));
   }

   void setCredentials(HttpClient httpClient, String username, String password) {
      Credentials defaultcreds = new UsernamePasswordCredentials(username, password);
      httpClient.getState().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM, AuthScope.ANY_SCHEME), defaultcreds);
      httpClient.getState().setAuthenticationPreemptive(true);
   }

   /**
    * Create a new {@link ClientHttpRequest} via this template's {@link ClientHttpRequestFactory}.
    * 
    * @param url
    *           the URL to connect to
    * @param method
    *           the HTTP method to execute (GET, POST, etc.)
    * @return the created request
    * @throws IOException
    *            in case of I/O errors
    */
   @Override
   protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
      String[] usernamePassword = CouchDbUtils.extractUsernamePassword(url);
      if(usernamePassword.length == 2) {
         setCredentials(httpClient, usernamePassword[0], usernamePassword[1]);
      }
      ClientHttpRequest request = getRequestFactory().createRequest(url, method);
      if (logger.isDebugEnabled()) {
         logger.debug("Created " + method.name() + " request for \"" + url.getScheme() + "://" + url.getHost() + ":" + url.getPort() + url.getPath() + "\"");
      }
      return request;
   }
}