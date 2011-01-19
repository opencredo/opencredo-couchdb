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
