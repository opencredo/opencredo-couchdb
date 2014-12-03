/*
 * Copyright 2011 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencredo.couchdb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeTrue;
import static org.springframework.http.HttpStatus.OK;

/**
 * Base class for CouchDB integration tests. Checks whether CouchDB is available before running each test,
 * in which case the test is executed. If CouchDB is not available, tests are ignored.
 * 
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 13/01/2011
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-couchdb.xml" })
public abstract class CouchDbIntegrationTest {

    protected static final Log log = LogFactory.getLog(CouchDbIntegrationTest.class);

    /**
     * Name of the design doc in the test database, which contains the views: * * {@value} , cf.
     * {@link #VIEW_DEF}
     */
    protected static final String DESIGNDOC = "testview";

    /** Name of one of the views: {@value} , cf. {@link #VIEW_DEF} */
    protected static final String BY_MESSAGE = "by_message";

    /** Name of one of the views: {@value} , cf. {@link #VIEW_DEF} */
    protected static final String BY_TIMESTAMP = "by_timestamp";

    /** Design document of the test DB, which contains two views: by_message and by_timestamp:
     * <pre>
     * {@value}
     * </pre>
     */
    protected static final String VIEW_DEF = "{ \"_id\": \"_design/testview\", \"language\": \"javascript\", \"views\": { \"by_message\": { \"map\": \"function(doc) { emit(doc.message, { message: doc.message, timestamp: doc.timestamp }); }\" }, \"by_timestamp\": { \"map\": \"function(doc) { emit(doc.timestamp, { message: doc.message, timestamp: doc.timestamp }); }\" } } } ";

    @Value("${couchdb.url}")
    private String couchDbUrl;

    @Value("${couchdb.testDatabase}")
    private String testDatabaseName;

    private String databaseUrl;

    protected static RestTemplate restTemplate;

    private String[] credentials = {};

    /**
     * This methods ensures that the database is running. Otherwise, the test is ignored.
     * 
     * @throws URISyntaxException
     */
    private void assumeDatabaseIsUpAndRunning() throws URISyntaxException {
        try {
            if (restTemplate == null) {
                synchronized (this) {
                    if (restTemplate == null) {
                        credentials = CouchDbUtils.extractUsernamePassword(databaseUrl());
                        if (credentials.length == 0)
                            restTemplate = new BasicAuthRestTemplate();
                        else
                            restTemplate = new BasicAuthRestTemplate(credentials[0], credentials[1], databaseUrl());
                    }
                }
            }
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(couchDbUrl, String.class);
            assumeTrue(responseEntity.getStatusCode().equals(OK));
            log.debug("CouchDB is running on " + couchDbUrl + " with status " + responseEntity.getStatusCode());
        } catch (RestClientException e) {
            log.debug("CouchDB is not running on " + couchDbUrl);
            assumeNoException(e);
        }
    }

    @Before
    public void setUpTestDatabase() throws Exception {
        assumeDatabaseIsUpAndRunning();
        RestTemplate template;
        if (credentials.length > 0)
            template = new BasicAuthRestTemplate(credentials[0], credentials[1], databaseUrl());
        else
            template = new BasicAuthRestTemplate();
        template.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                // do nothing, error status will be handled in the switch statement
            }
        });
        ResponseEntity<String> response = template.getForEntity(databaseUrl(), String.class);
        HttpStatus statusCode = response.getStatusCode();
        switch (statusCode) {
        case NOT_FOUND:
            createNewTestDatabase();
            break;
        case OK:
            deleteExisitingTestDatabase();
            createNewTestDatabase();
            break;
        default:
            throw new IllegalStateException("Unsupported http status [" + statusCode + "]");
        }
    }

    private void deleteExisitingTestDatabase() {
        restTemplate.delete(databaseUrl());
    }

    private void createNewTestDatabase() {
        restTemplate.put(databaseUrl(), null);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> body = new HttpEntity<String>(VIEW_DEF, headers);
        URI result = restTemplate.postForLocation(databaseUrl(), body);
        log.debug("Result of POST to " + databaseUrl + " is " + result);
    }

    /**
     * Reads a CouchDB document and converts it to the expected type.
     */
    protected <T> T getDocument(String id, Class<T> expectedType) {
        String url = databaseUrl() + "{id}";
        return restTemplate.getForObject(url, expectedType, id);
    }

    /**
     * Writes a CouchDB document
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected String putDocument(Object document) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity request = new HttpEntity(document, headers);
        String id = UUID.randomUUID().toString();
        restTemplate.put(databaseUrl() + "{id}", request, id);
        return id;
    }

    public String databaseUrl() {
        if (databaseUrl != null)
            return databaseUrl;
        StringBuilder b = new StringBuilder(couchDbUrl);
        if (couchDbUrl.endsWith("/")) {
            b.append(testDatabaseName);
        } else {
            b.append('/');
            b.append(testDatabaseName);
        }
        databaseUrl = b.toString();
        return databaseUrl;
    }
}
