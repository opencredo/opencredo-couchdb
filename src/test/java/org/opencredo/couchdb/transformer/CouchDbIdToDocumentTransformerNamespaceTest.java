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

package org.opencredo.couchdb.transformer;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencredo.couchdb.DummyDocument;
import org.opencredo.couchdb.core.CouchDbDocumentOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
/**
 * @author Tareq Abedrabbo
 * @since 25/01/2011
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CouchDbIdToDocumentTransformerNamespaceTest {

    @Autowired
    private CouchDbDocumentOperations documentOperations;

    @Autowired
    private MessagingTemplate messagingTemplate;

    @Test
    public void transformStringId() throws Exception {
        String id = "id";
        DummyDocument document = new DummyDocument("test");
        when(documentOperations.readDocument(eq(id), eq(DummyDocument.class))).thenReturn(document);
        Object response = messagingTemplate.convertSendAndReceive(id, DummyDocument.class);
        assertThat(response, instanceOf(DummyDocument.class));
        DummyDocument responseDocument = (DummyDocument) response;
        assertThat(responseDocument, equalTo(document));
    }

    @After
    public void tearDown() throws Exception {
        reset(documentOperations);
    }
}
