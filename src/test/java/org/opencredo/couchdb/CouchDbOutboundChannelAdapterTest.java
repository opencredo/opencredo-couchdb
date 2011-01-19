package org.opencredo.couchdb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.Message;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 17/01/2011
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CouchDbOutboundChannelAdapterTest extends CouchDbTest {

    @Autowired
    private MessagingTemplate messagingTemplate;

    @Test
    public void sendMessage() {
        TestDocument document = new TestDocument("klaatu berada nikto");
        Message<TestDocument> message = MessageBuilder.withPayload(document).build();
        logger.debug("message id = {}", message.getHeaders().getId());
        messagingTemplate.send(message);
    }
}
