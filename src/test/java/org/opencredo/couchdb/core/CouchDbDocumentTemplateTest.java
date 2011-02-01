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
import org.opencredo.couchdb.IsBodyEqual;
import org.springframework.web.client.RestOperations;

import java.util.UUID;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.opencredo.couchdb.IsBodyEqual.bodyEqual;

/**
 * @author Tareq Abedrabbo
 * @since 31/01/2011
 */
public class CouchDbDocumentTemplateTest {

    private CouchDbDocumentTemplate documentTemplate;
    private RestOperations restOperations;

    @Before
    public void setUp() throws Exception {
        restOperations = mock(RestOperations.class);
        documentTemplate = new CouchDbDocumentTemplate("test", restOperations);
    }

    @Test
    public void createDocument() throws Exception {
        DummyDocument document = new DummyDocument("hello");
        String id = UUID.randomUUID().toString();
        documentTemplate.writeDocument(id, document);
        verify(restOperations).put(anyString(), argThat(bodyEqual(document)), eq(id));
    }
}
