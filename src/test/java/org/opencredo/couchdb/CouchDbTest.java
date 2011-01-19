package org.opencredo.couchdb;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeTrue;

/**
 * Base class for CouchDB tests.
 * @since 13/01/2011
 */
public abstract class CouchDbTest {


    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final static String FEED_NONE = "";
    protected final static String FEED_LONG_POLL = "longpoll";


    protected static final String DEFAULT_DATABASE_URL = "http://127.0.0.1:5984/messages/";
    protected String databaseUrl = DEFAULT_DATABASE_URL;
    protected final RestTemplate testTemplate = new RestTemplate();

    /**
     * This methods ensures that the database is running before every test. Otherwise, the test is ignored.
     */
    @Before
    public void assumeDatabaseIsUpAndRunning() {
        try {
            ResponseEntity<String> responseEntity = testTemplate.getForEntity(databaseUrl, String.class);
            assumeTrue(responseEntity.getStatusCode().equals(HttpStatus.OK));
            logger.debug("CouchDB is running on {} with status {} ", databaseUrl, responseEntity.getStatusCode());
        } catch (RestClientException e) {
            logger.debug("CouchDB is not running on {}", databaseUrl);
            assumeNoException(e);
        }
    }

    protected <T> T getDocument(String id, Class<T> expectedType) {
        String url = databaseUrl + "{id}";
        return testTemplate.getForObject(url, expectedType, id);
    }
}
