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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opencredo.couchdb.core.CouchDbDocumentOperations;
import org.opencredo.couchdb.core.CouchDbDocumentTemplate;
import org.springframework.messaging.Message;
import org.springframework.integration.context.IntegrationObjectSupport;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * A MessageSource that takes the URI of a CouchDB view or database and receives all messages sequentially by using the skip and limit parameters
 * which are supported by CouchDB. The retrieval of the documents is performed through an instance of CouchDbDocumentOperations.
 * <p/>
 * Each request returns and stores internally a number of {@link JsonNode}s equal to the queue capacity; when the queue becomes empty, a new request
 * is made with limit and skip adjusted accordingly.
 * 
 * @author Kambiz Darabi
 * @since 2014-02-18
 */
public class CouchDbAllDocumentsMessageSource extends IntegrationObjectSupport implements MessageSource<URI> {

   private final int limit;

   /**
    * The number of documents which we already retrieved.
    */
   private int skip = 0;

   final CouchDbDocumentOperations couchDbDocumentOperations;

   private final Queue<URI> toBeReceived;

   /**
    * The base URI of the database or view to which <code>limit</code> and <code>skip</code> query parameters are added.
    */
   private final URI databaseUri;

   private String baseUri;

   /**
    * Creates an instance with a default database to connect to.
    * 
    * @throws URISyntaxException
    */
   public CouchDbAllDocumentsMessageSource(String databaseUrl, int limit) throws URISyntaxException {
      this.couchDbDocumentOperations = new CouchDbDocumentTemplate(databaseUrl);
      this.limit = limit;
      this.toBeReceived = new ArrayBlockingQueue<URI>(limit);
      this.databaseUri = new URI(databaseUrl);
      int ind = databaseUrl.indexOf("/_all_docs");
      if (ind != -1) {
         this.baseUri = databaseUrl.substring(0, ind);
      } else {
         ind = databaseUrl.indexOf("/_design/");
         if (ind != -1) {
            this.baseUri = databaseUrl.substring(0, ind);
         } else {
            throw new IllegalArgumentException("databaseUrl must be a /_design/../_view/... or a /_all_docs URL");
         }
      }
   }

   public Message<URI> receive() {
      if (toBeReceived.isEmpty()) {
         URI skipUri = UriComponentsBuilder.fromUri(databaseUri).replaceQueryParam("limit", limit).replaceQueryParam("skip", skip).build().toUri();
         ObjectNode response = couchDbDocumentOperations.readDocument(skipUri, ObjectNode.class);
         ArrayNode rows = (ArrayNode) response.get("rows");
         int size = rows.size();
         Assert.isTrue(size <= limit, "Retrieved more rows than limit");
         for (int i = 0; i < size; i++) {
            JsonNode node = rows.get(i);
            String id = node.get("id").textValue();
            try {
               toBeReceived.add(new URI(baseUri + "/" + id));
               skip++;
            } catch (URISyntaxException e) {
               logger.error("Error creating the URI of document from baseUri and ID", e);
               return null;
            }
         }
      }

      Map<String, String> headers = createHeaderMap(databaseUri, skip, limit);
      return prepareMessage(toBeReceived.poll(), headers);
   }

   Map<String, String> createHeaderMap(URI uri, int skip, int limit) {
      Map<String, String> headers = new HashMap<String, String>(4);
      headers.put("couchdb-uri", uri.toString());
      headers.put("couchdb-skip", String.valueOf(skip));
      headers.put("couchdb-limit", String.valueOf(limit));
      return headers;
   }

   private Message<URI> prepareMessage(URI uri, Map<String, String> headers) {
      Message<URI> message = null;
      if (uri != null) {
         message = MessageBuilder.withPayload(uri).copyHeaders(headers).build();
      }
      return message;
   }
}
