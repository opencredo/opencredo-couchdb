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
import org.opencredo.couchdb.transformer.CouchDbIdToDocumentTransformer;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.client.RestOperations;

import java.util.UUID;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 21/01/2011
 */
public class CouchDbIdToDocumentTransformerTest {

    private CouchDbIdToDocumentTransformer transformer;
    private RestOperations restOperations;
    

    @Before
    public void setUp() throws Exception {
        restOperations = mock(RestOperations.class);
        transformer = new CouchDbIdToDocumentTransformer("xxx", DummyDocument.class, restOperations);
    }

    @Test
    public void transformSimpleMessage() throws Exception {
        String uuid = UUID.randomUUID().toString();
        DummyDocument document = new DummyDocument("test");
        when(restOperations.getForObject(anyString(), eq(DummyDocument.class), eq(uuid))).thenReturn(document);


        Message<String> message = MessageBuilder.withPayload(uuid).build();
        Message<DummyDocument> transformedMessage = (Message<DummyDocument>) transformer.transform(message);

        assertThat(transformedMessage.getPayload(), equalTo(document));
    }
}
