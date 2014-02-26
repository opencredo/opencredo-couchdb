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

import org.junit.Before;
import org.junit.Test;
import org.opencredo.couchdb.DummyDocument;
import org.springframework.integration.MessageHeaders;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.util.HashMap;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.opencredo.couchdb.IsBodyEqual.bodyEqual;

/**
 * @author Tareq Abedrabbo
 * @since 31/01/2011
 */
public class CouchDbDocumentTemplateTest {

    protected static final String DEFAULT_DATABASE_URL = "http://test";
    private CouchDbDocumentTemplate documentTemplate;
    private RestOperations restOperations;

    @Before
    public void setUp() throws Exception {
        restOperations = mock(RestOperations.class);
        documentTemplate = new CouchDbDocumentTemplate(DEFAULT_DATABASE_URL);
        documentTemplate.setRestOperations(restOperations);
    }

    @Test
    public void writeDocumentToDefaultDatabase() throws Exception {
        DummyDocument document = new DummyDocument("hello");
        String id = UUID.randomUUID().toString();
        documentTemplate.writeDocument(id, document);
        verify(restOperations).put(anyString(), argThat(bodyEqual(document)), eq(id));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void writeDocumentToUri() throws Exception {
        DummyDocument document = new DummyDocument("hello");
        URI uri = new URI("http://test");
        documentTemplate.writeDocument(uri, document);
        verify(restOperations).put(eq(uri.toString()), argThat(bodyEqual(document)), anyMap());
    }

    @Test
    public void writeDocumentToUriWithHeaders() throws Exception {
        DummyDocument document = new DummyDocument("hello");
        URI uri = new URI("http://test");
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("id", "specified_id");
        MessageHeaders headers = new MessageHeaders(map);
        documentTemplate.writeDocument(uri, document, headers);
        verify(restOperations).put(eq(uri.toString()), argThat(bodyEqual(document)), eq(headers));
    }

    @Test
    public void readDocumentFromDefaultDatabase() throws Exception {
        String id = UUID.randomUUID().toString();
        DummyDocument response = new DummyDocument("test");
        when(restOperations.getForObject(anyString(), eq(DummyDocument.class), eq(id))).
                thenReturn(response);
        DummyDocument result = documentTemplate.readDocument(id, DummyDocument.class);
        assertThat(result, equalTo(response));
    }

    @Test
    public void readDocumentFromUri() throws Exception {
        URI uri = new URI("http://test");
        DummyDocument response = new DummyDocument("test");
        when(restOperations.getForObject(eq(uri), eq(DummyDocument.class))).
                thenReturn(response);
        DummyDocument result = documentTemplate.readDocument(uri, DummyDocument.class);
        assertThat(result, equalTo(response));
    }
}
