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

import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeTrue;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Base class for CouchDB tests.
 *
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
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
