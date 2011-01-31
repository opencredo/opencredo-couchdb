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

import org.springframework.integration.Message;
import org.springframework.integration.context.IntegrationObjectSupport;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collection;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Tareq Abedrabbo
 * @author Tomas Lukosius
 * @since 24/01/2011
 */
public class CouchDbChangesPollingMessageSource extends IntegrationObjectSupport implements MessageSource<URI> {


    private static final int DEFAULT_INTERNAL_QUEUE_CAPACITY = 5;


    private final Queue<ChangedDocument> toBeReceived;

    private final ChangesPoller changesPoller;

    public CouchDbChangesPollingMessageSource(ChangesPoller changesPoller) {
        this.changesPoller = changesPoller;
        this.toBeReceived = new PriorityBlockingQueue<ChangedDocument>(
                DEFAULT_INTERNAL_QUEUE_CAPACITY, new Comparator<ChangedDocument>() {
                    public int compare(ChangedDocument doc1, ChangedDocument doc2) {
                        long diff = doc1.getSequence() - doc2.getSequence();
                        if (diff == 0) {
                            return 0;
                        } else {
                            return diff > 0 ? 1 : -1;
                        }
                    }
                });
    }

    public CouchDbChangesPollingMessageSource(String databaseUrl) {
        this(new DefaultChangesPoller(databaseUrl, new RestTemplate()));
    }

    public Message<URI> receive() {
        if (toBeReceived.isEmpty()) {
            Collection<ChangedDocument> changedDocuments = changesPoller.pollForChanges();
            toBeReceived.addAll(changedDocuments);
        }

        ChangedDocument changedDocument = toBeReceived.poll();
        return prepareMessage(changedDocument);
    }

    private Message<URI> prepareMessage(ChangedDocument changedDocument) {
        Message<URI> message = null;
        if (changedDocument != null) {
            message = MessageBuilder.withPayload(changedDocument.getUri()).build();
        }
        return message;
    }
}
