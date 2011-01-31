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

import org.opencredo.couchdb.CouchDbUtils;
import org.springframework.integration.MessagingException;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tareq Abedrabbo
 * @author Tomas Lukosius
 * @since 24/01/2011
 */
public class DefaultChangesPoller implements ChangesPoller {


    private final RestOperations restOperations;

    private final String databaseUrl;

    private long currentSequence = 0L;

    public DefaultChangesPoller(String databaseUrl, RestOperations restOperations) {
        this.databaseUrl = databaseUrl;
        this.restOperations = restOperations;
    }

    public Collection<ChangedDocument> pollForChanges() {
        Changes changes = restOperations.getForObject(CouchDbUtils.addChangesSince(databaseUrl),
                Changes.class, currentSequence);

        Long lastSequence = changes.getLast_seq();
        if (lastSequence > currentSequence) {
            Collection<ChangedDocument> changedDocuments = prepareChanges(changes);
            currentSequence = lastSequence;
            return changedDocuments;
        } else return Collections.EMPTY_SET;
    }

    private Collection<ChangedDocument> prepareChanges(Changes changes) {
        Set<ChangedDocument> changedDocuments = new HashSet<ChangedDocument>();

        for (Change change : changes.getResults()) {
            try {
                URI uri = new URI(CouchDbUtils.ensureTrailingSlash(databaseUrl) + change.getId());
                ChangedDocument.Status status = determineStatus(change);
                changedDocuments.add(new ChangedDocument(uri, status, change.getSeq()));

            } catch (URISyntaxException e) {
                throw new MessagingException("unable to create URI for [" + change + "]", e);
            }
        }
        return changedDocuments;
    }

    private ChangedDocument.Status determineStatus(Change change) {
        if (change.deleted) {
            return ChangedDocument.Status.DELETED;
        } else if (change.changes.get(0).rev.startsWith("1-")) {
            return ChangedDocument.Status.CREATED;
        } else {
            return ChangedDocument.Status.UPDATED;
        }
    }

    static class Changes {
        private List<Change> results;
        Long last_seq;

        public List<Change> getResults() {
            return results;
        }

        public void setResults(List<Change> results) {
            this.results = results;
        }

        public Long getLast_seq() {
            return last_seq;
        }

        public void setLast_seq(Long last_seq) {
            this.last_seq = last_seq;
        }

        @Override
        public String toString() {
            return "Changes{" +
                    "last_seq=" + last_seq +
                    ", results=" + results +
                    '}';
        }
    }

    static class Change {
        Long seq;
        String id;
        List<Revision> changes;
        boolean deleted;

        public boolean getDeleted() {
            return deleted;
        }

        public void setDeleted(boolean deleted) {
            this.deleted = deleted;
        }

        public List<Revision> getChanges() {
            return changes;
        }

        public void setChanges(List<Revision> changes) {
            this.changes = changes;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Long getSeq() {
            return seq;
        }

        public void setSeq(Long seq) {
            this.seq = seq;
        }

        @Override
        public String toString() {
            return "Change{" +
                    "changes=" + changes +
                    ", seq=" + seq +
                    ", id='" + id + '\'' +
                    '}';
        }
    }

    static class Revision {
        String rev;

        public String getRev() {
            return rev;
        }

        public void setRev(String rev) {
            this.rev = rev;
        }

        @Override
        public String toString() {
            return "Revision{" +
                    "rev='" + rev + '\'' +
                    '}';
        }
    }


}
