package org.opencredo.couchdb;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.integration.Message;
import org.springframework.integration.support.MessageBuilder;

/**
 * @since 11/01/2011
 */
public class CouchDbSendingMessageHandlerTest extends CouchDbTest {

    private CouchDbSendingMessageHandler messageHandler;

    @Before
    public void setUp() {
        messageHandler = new CouchDbSendingMessageHandler(databaseUrl);
    }

    @Test
    public void handleMessage() throws Exception {
        TestDocument document = new TestDocument("Klaatu Berada Nikto");
        Message<TestDocument> message = MessageBuilder.withPayload(document).build();
        logger.debug("message id = {}", message.getHeaders().getId());
        messageHandler.handleMessage(message);

        //assert message in the database
        TestDocument result = getDocument(message.getHeaders().getId().toString(), TestDocument.class);
        logger.debug("result: [{}]", result);
        assertThat(document.getMessage(), equalTo(result.getMessage()));

    }
}
