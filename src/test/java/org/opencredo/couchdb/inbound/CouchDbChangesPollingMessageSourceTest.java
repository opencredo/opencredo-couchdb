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

import org.junit.Before;
import org.junit.Test;
import org.opencredo.couchdb.inbound.ChangedDocument;
import org.opencredo.couchdb.inbound.ChangesPoller;
import org.opencredo.couchdb.inbound.CouchDbChangesPollingMessageSource;
import org.springframework.integration.Message;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Tareq Abedrabbo
 * @since 24/01/2011
 */
public class CouchDbChangesPollingMessageSourceTest {


    private static final int NUMBER_OF_CHANGES = 10;
    private ChangesPoller changesPoller;

    private CouchDbChangesPollingMessageSource messageSource;

    @Before
    public void setUp() throws Exception {
        changesPoller = mock(ChangesPoller.class);
        messageSource = new CouchDbChangesPollingMessageSource(changesPoller);
    }

    @Test
    public void receiveOnNonEmptyPoll() throws Exception {
        when(changesPoller.pollForChanges()).thenReturn(createChangedDocuments(NUMBER_OF_CHANGES));
        for (int i = 0; i < NUMBER_OF_CHANGES; i++) {
            Message<URI> message = messageSource.receive();
            assertThat(message, is(notNullValue()));
        }
    }

    @Test
    public void receiveOnEmptyPoll() throws Exception {
        when(changesPoller.pollForChanges()).thenReturn(Collections.<ChangedDocument>emptySet());
        Message<URI> message = messageSource.receive();
        assertThat(message, is(nullValue()));
    }


    private Collection<ChangedDocument> createChangedDocuments(int n) throws URISyntaxException {
        Set<ChangedDocument> documents = new HashSet<ChangedDocument>();
        String uri = "http://test/database/";
        for (int i = 0; i < n; i++) {
            ChangedDocument doc = new ChangedDocument(new URI(uri + UUID.randomUUID()), ChangedDocument.Status.CREATED, new Long(i));
            documents.add(doc);
        }
        return documents;
    }
}
