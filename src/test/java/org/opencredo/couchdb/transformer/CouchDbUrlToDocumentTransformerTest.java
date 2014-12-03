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

import org.junit.Before;
import org.junit.Test;
import org.opencredo.couchdb.DummyDocument;
import org.opencredo.couchdb.core.CouchDbDocumentOperations;
import org.springframework.messaging.Message;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.transformer.MessageTransformationException;

import java.net.URI;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 01/02/2011
 */
public class CouchDbUrlToDocumentTransformerTest {

    private CouchDbUrlToDocumentTransformer transformer;
    private CouchDbDocumentOperations documentOperations;


    @Before
    public void setUp() throws Exception {
        documentOperations = mock(CouchDbDocumentOperations.class);
        transformer = new CouchDbUrlToDocumentTransformer(DummyDocument.class, documentOperations);
    }

    @Test
    public void transformUriMessage() throws Exception {
        URI uri = new URI("http://test");
        DummyDocument document = new DummyDocument("test");
        when(documentOperations.readDocument(eq(uri), eq(DummyDocument.class))).thenReturn(document);


        Message<URI> message = MessageBuilder.withPayload(uri).build();
        Message<DummyDocument> transformedMessage = (Message<DummyDocument>) transformer.transform(message);

        assertThat(transformedMessage.getPayload(), equalTo(document));
    }

    @Test
    public void transformStringMessage() throws Exception {
        String uri = "http://test";
        DummyDocument document = new DummyDocument("test");
        when(documentOperations.readDocument(eq(new URI(uri)), eq(DummyDocument.class))).thenReturn(document);


        Message<String> message = MessageBuilder.withPayload(uri).build();
        Message<DummyDocument> transformedMessage = (Message<DummyDocument>) transformer.transform(message);

        assertThat(transformedMessage.getPayload(), equalTo(document));
    }

    @Test(expected = MessageTransformationException.class)
    public void transformUnkownIdType() throws Exception {
        transformer.transform(MessageBuilder.withPayload('a').build());
    }
}
