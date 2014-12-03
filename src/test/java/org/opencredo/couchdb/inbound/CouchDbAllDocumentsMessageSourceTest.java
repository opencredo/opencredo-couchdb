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

package org.opencredo.couchdb.inbound;

import static org.junit.Assert.*;

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencredo.couchdb.CouchDbIntegrationTest;
import org.opencredo.couchdb.DummyDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.Message;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.endpoint.AbstractEndpoint;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Kambiz Darabi <darabi@m-creations.net>
 * @since 2014-02-18
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CouchDbAllDocumentsMessageSourceTest extends CouchDbIntegrationTest {

   private static final int TEST_MESSAGES_NUMBER = 20;

   @Autowired
   @Qualifier("inboundChannelAdapter")
   private AbstractEndpoint inboundChannelAdapter;

   @Autowired
   private MessagingTemplate messagingTemplate;

   /* this one is needed to retrieve messages directly from DB */
   private CouchDbAllDocumentsMessageSource messageSource;

   @Before
   public void setUp() throws Exception {
      createTestDocuments();
      messageSource = new CouchDbAllDocumentsMessageSource(databaseUrl() + "_all_docs", 10);
   }

   @Test
   public void receiveSomeDocuments() throws Exception {
      inboundChannelAdapter.start();
      JsonNode row = receiveJson();
      assertEquals("test message 0", row.get("message").textValue());
      // messages 1, 10, 11, 12 are the next ones
      throwAwayMessages(4);
      // the next one should be 13
      row = receiveJson();
      assertEquals("test message 13", row.get("message").textValue());
      throwAwayMessages(13);
      // the alphabetically last message is 9
      row = receiveJson();
      assertEquals("test message 9", row.get("message").textValue());
      // the next one should be null
      row = receiveJson();
      assertNull(null);
   }

   void throwAwayMessages(int count) {
      for (int i = 0; i < count; i++) {
         receiveJson();
      }
   }

   private JsonNode receiveJson() {
      Message<URI> msg = (Message<URI>) messagingTemplate.receive();
      if(msg == null)
         return null;
      msg.getPayload();
      return messageSource.couchDbDocumentOperations.readDocument(msg.getPayload(), JsonNode.class);
   }

   private void createTestDocuments() {
      for (int i = 0; i < TEST_MESSAGES_NUMBER; i++) {
         DummyDocument document = new DummyDocument("test message " + i);
         putDocument(document);
      }
   }
}
