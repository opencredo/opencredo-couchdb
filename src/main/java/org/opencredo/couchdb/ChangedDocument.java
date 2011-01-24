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

import java.net.URI;

/**
 * @author Tareq Abedrabbo
 * @since 24/01/2011
 */
public class ChangedDocument {

    public enum Status {
        CREATED, UPDATED, DELETED;
    }

    private final URI uri;

    private final Status status;

    private final Long sequence;

    public ChangedDocument(URI uri, Status status, Long sequence) {
        this.status = status;
        this.uri = uri;
        this.sequence = sequence;
    }

    public URI getUri() {
        return uri;
    }

    public Status getStatus() {
        return status;
    }

    public Long getSequence() {
        return sequence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChangedDocument that = (ChangedDocument) o;

        if (sequence != null ? !sequence.equals(that.sequence) : that.sequence != null) return false;
        if (status != that.status) return false;
        if (uri != null ? !uri.equals(that.uri) : that.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uri != null ? uri.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChangedDocument{" +
                "sequence=" + sequence +
                ", uri=" + uri +
                ", status=" + status +
                '}';
    }

    //    public boolean isCreated() {
//        return status == Status.CREATED;
//    }
//
//    public boolean isUpdated() {
//        return status == Status.UPDATED;
//    }
//
//    public boolean isDeleted() {
//        return status == Status.DELETED;
//    }
}
