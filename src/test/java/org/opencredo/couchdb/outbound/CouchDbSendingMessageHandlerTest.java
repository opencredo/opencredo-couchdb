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

package org.opencredo.couchdb.outbound;

import java.util.HashMap;

import org.junit.Test;
import org.opencredo.couchdb.CouchDbIntegrationTest;
import org.opencredo.couchdb.DummyDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 11/01/2011
 */
@ContextConfiguration
public class CouchDbSendingMessageHandlerTest extends CouchDbIntegrationTest {

    @Autowired
    @Qualifier("messageHandler")
    private CouchDbSendingMessageHandler messageHandler;

    @Autowired
    @Qualifier("messageHandlerTemplateUrl")
    private CouchDbSendingMessageHandler messageHandlerTemplateUrl;

    @Test
    public void handleMessage() throws Exception {
        DummyDocument document = new DummyDocument("Klaatu Berada Nikto");
        Message<DummyDocument> message = MessageBuilder.withPayload(document).build();
        messageHandler.handleMessage(message);

        //assert message in the database
        DummyDocument result = getDocument(message.getHeaders().getId().toString(), DummyDocument.class);
        assertThat(document.getMessage(), equalTo(result.getMessage()));

    }

    @Test
    public void handleMessageTemplateUrl() throws Exception {
        DummyDocument document = new DummyDocument("We use template variables in our URL");
        HashMap<String, Object> headers = new HashMap<String, Object>();
        headers.put("greatDatabase", "couchdb");
        Message<DummyDocument> message = MessageBuilder.withPayload(document).copyHeaders(headers).build();
        messageHandlerTemplateUrl.handleMessage(message);

        //assert message in the database
        DummyDocument result = getDocument(message.getHeaders().getId().toString(), DummyDocument.class);
        assertThat(document.getMessage(), equalTo(result.getMessage()));

    }
}
