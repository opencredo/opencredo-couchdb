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

package org.opencredo.couchdb.core;

import org.opencredo.couchdb.CouchDbUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.HashMap;

/**
 * An implementation of CouchDbDocumentOperations that relies on RestOperations to communicate
 * with CouchDB
 * 
 * @author Tareq Abedrabbo
 * @since 31/01/2011
 */
public class CouchDbDocumentTemplate extends CouchDbObjectSupport implements CouchDbDocumentOperations {

    private String defaultDocumentUrl;

    /**
     * The default constructor.
     */
    public CouchDbDocumentTemplate() {
       super();
    }

    /**
     * Constructs an instance of CouchDbDocumentTemplate with a default database
     * @param defaultDatabaseUrl the default database to connect to
     */
    public CouchDbDocumentTemplate(String defaultDatabaseUrl) {
        super(defaultDatabaseUrl);
        Assert.hasText(defaultDatabaseUrl, "defaultDatabaseUrl must not be empty");
        setDefaultDocumentUrl(defaultDatabaseUrl);
    }

    /**
     * Constructs an instance of CouchDbDocumentTemplate with a default database, user, and password for Basic Authentication
     * 
     * @param defaultDatabaseUrl the default database to connect to
     */
    public CouchDbDocumentTemplate(String defaultDatabaseUrl, String username, String password) {
        super(username, password, defaultDatabaseUrl);
        Assert.hasText(defaultDatabaseUrl, "defaultDatabaseUrl must not be empty");
        setDefaultDocumentUrl(defaultDatabaseUrl);
    }

    private void setDefaultDocumentUrl(String defaultDatabaseUrl) {
        if(defaultDatabaseUrl.contains("{id}"))
            defaultDocumentUrl = defaultDatabaseUrl;
        else
            defaultDocumentUrl = CouchDbUtils.addId(defaultDatabaseUrl);
    }

    public <T> T readDocument(String id, Class<T> documentType) throws CouchDbOperationException {
        Assert.state(defaultDocumentUrl != null, "defaultDatabaseUrl must be set to use this method");
        try {
            return restOperations.getForObject(defaultDocumentUrl, documentType, id);
        } catch (RestClientException e) {
            throw new CouchDbOperationException("Unable to communicate with CouchDB", e);
        }
    }

    public <T> T readDocument(URI uri, Class<T> documentType) throws CouchDbOperationException {
        try {
            return restOperations.getForObject(uri, documentType);
        } catch (RestClientException e) {
            throw new CouchDbOperationException("Unable to communicate with CouchDB", e);
        }
    }

    public void writeDocument(String id, Object document) throws CouchDbOperationException {
        writeDocument(id, document, null);
    }
    public void writeDocument(String id, Object document, MessageHeaders headers) throws CouchDbOperationException {
        Assert.state(defaultDocumentUrl != null, "defaultDatabaseUrl must be set to use this method");
        HttpEntity<?> httpEntity = createHttpEntity(document);
        try {
            if(headers != null) {
                HashMap<String, Object> copiedHeaders = new HashMap<String, Object>(headers);
                if(StringUtils.hasLength(id)) {
                    copiedHeaders.put(MessageHeaders.ID, id);
                }
                restOperations.put(defaultDocumentUrl, httpEntity, copiedHeaders);
            } else {
                restOperations.put(defaultDocumentUrl, httpEntity, id);
            }
        } catch (RestClientException e) {
            throw new CouchDbOperationException("Unable to communicate with CouchDB", e);
        }
    }

    public void writeDocument(URI uri, Object document) throws CouchDbOperationException {
        writeDocument(uri, document, null);
    }

    public void writeDocument(URI uri, Object document, MessageHeaders headers) throws CouchDbOperationException {
        HttpEntity<?> httpEntity = createHttpEntity(document);
        try {
            restOperations.put(uri.toString(), httpEntity, headers);
        } catch (RestClientException e) {
            throw new CouchDbOperationException("Unable to communicate with CouchDB", e);
        }
    }

    /** Sets RestOperations */
    public void setRestOperations(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    private HttpEntity<?> createHttpEntity(Object document) {

        if (document instanceof HttpEntity) {
            HttpEntity<?> httpEntity = (HttpEntity<?>) document;
            Assert.isTrue(httpEntity.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON),
                    "HttpEntity payload with non application/json content type found.");
            return httpEntity;
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(document, httpHeaders);

        return httpEntity;
    }
}
