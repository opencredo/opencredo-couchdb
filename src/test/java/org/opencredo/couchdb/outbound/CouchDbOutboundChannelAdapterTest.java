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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencredo.couchdb.CouchDbIntegrationTest;
import org.opencredo.couchdb.DummyDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 17/01/2011
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:org/opencredo/couchdb/outbound/CouchDbOutboundChannelAdapterTest-context.xml" })
public class CouchDbOutboundChannelAdapterTest extends CouchDbIntegrationTest {

    @Autowired
    private MessagingTemplate messagingTemplate;

    @Test
    public void sendMessage() {
        DummyDocument document = new DummyDocument("klaatu berada nikto");
        Message<DummyDocument> message = MessageBuilder.withPayload(document).build();
        messagingTemplate.send(message);
        DummyDocument response = getDocument("test_id", DummyDocument.class);
        assertThat(response, equalTo(document));
    }
}
