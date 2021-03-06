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

package org.opencredo.couchdb.core;

import org.opencredo.couchdb.CouchDbUtils;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An implementation of CouchDbChangesOperations that relies on a RestOperations to communicate
 * with CouchDB.
 *
 * @author Tareq Abedrabbo
 * @author Tomas Lukosius
 * @since 24/01/2011
 */
public class CouchDbChangesTemplate extends CouchDbObjectSupport implements CouchDbChangesOperations {

    private String databaseUrl;

    private String databaseChangesUrl;

    private long currentSequence = 0L;

    /**
     * The default constructor.
     */
    public CouchDbChangesTemplate() {
    }

    /**
     * Creates an instance of CouchDbChangesTemplate with a default database.
     *
     * @param defaultDatabaseUrl the default database to connect to
     */
    public CouchDbChangesTemplate(String defaultDatabaseUrl) {
        Assert.hasText(defaultDatabaseUrl, "defaultDatabaseUrl must not be empty");
        this.databaseUrl = defaultDatabaseUrl;
        databaseChangesUrl = CouchDbUtils.addChangesSince(defaultDatabaseUrl);
    }

    public List<ChangedDocument> pollForChanges() throws CouchDbOperationException {
        if (logger.isDebugEnabled()) {
            logger.debug("polling " + databaseUrl + " for changes from sequence " + currentSequence);
        }
        Changes changes = null;
        try {
            changes = restOperations.getForObject(databaseChangesUrl,
                    Changes.class, currentSequence);
        } catch (RestClientException e) {
            throw new CouchDbOperationException("Unable to communicate with CouchDB", e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("found " + changes.getResults().size() + " changes");
        }

        Long lastSequence = changes.getLast_seq();
        if (lastSequence > currentSequence) {
            List<ChangedDocument> changedDocuments = prepareChanges(changes);
            currentSequence = lastSequence;
            return changedDocuments;
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    /**
     * Sets RestOperations
     */
    public void setRestOperations(RestOperations restOperations) {
        this.restOperations = restOperations;
    }

    private List<ChangedDocument> prepareChanges(Changes changes) {
        List<ChangedDocument> changedDocuments = new ArrayList<ChangedDocument>();

        for (Change change : changes.getResults()) {
            try {
                URI uri = new URI(CouchDbUtils.ensureTrailingSlash(databaseUrl) + change.getId());
                ChangedDocument.Status status = determineStatus(change);
                changedDocuments.add(new ChangedDocument(uri, status, change.getSeq()));
            } catch (URISyntaxException e) {
                throw new CouchDbOperationException("unable to create URI for [" + change + "]", e);
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
