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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHandlingException;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.annotation.Repeat;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.sql.Date;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;


/**
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 17/01/2011
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CouchDbOutboundChannelAdapterPollerTest extends CouchDbTest {

    @Autowired
    private MessagingTemplate messagingTemplate;

    @Test
    @Repeat(5)
    public void sendMessage() throws Exception {
        TestDocument document = new TestDocument("polling test - " + UUID.randomUUID());
        Message<TestDocument> message = MessageBuilder.withPayload(document).build();
        messagingTemplate.send(message);

        TestDocument response = null;
        for(int i = 0; i < 3; i++) {
            try {
                response = (TestDocument) messagingTemplate.convertSendAndReceive("testRequestChannel", message.getHeaders().getId());
                break;
            } catch (MessageHandlingException e) {
                Thread.sleep(1000);
            }
        }
        assertThat(document.getMessage(), equalTo(response.getMessage()));

    }
}
