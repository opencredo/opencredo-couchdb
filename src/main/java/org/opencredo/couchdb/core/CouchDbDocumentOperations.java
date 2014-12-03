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

import java.net.URI;

import org.springframework.messaging.MessageHeaders;

/**
 * CouchDB operations that allow to read and write documents from and to the database.
 *
 * @author Tareq Abedrabbo
 * @since 31/01/2011
 */
public interface CouchDbDocumentOperations {

    /**
     * Reads a document from the database and maps it a Java object.
     * </p>
     * This method is intended to work when a default database
     * is set on the CouchDbDocumentOperations instance.
     *
     * @param id           the id of the CouchDB document to read
     * @param documentType the target type to map to
     * @return the mapped object
     */
    <T> T readDocument(String id, Class<T> documentType) throws CouchDbOperationException;

    /**
     * Reads a document from the database and maps it a Java object.
     *
     * @param uri          the full URI of the document to read
     * @param documentType the target type to map to
     * @return the mapped object
     */
    <T> T readDocument(URI uri, Class<T> documentType) throws CouchDbOperationException;

    /**
     * Maps a Java object to JSON and writes it to the database
     * </p>
     * This method is intended to work when a default database
     * is set on the CouchDbDocumentOperations instance.
     *
     * @param id       the id of the document to write
     * @param document the object to write
     */
    void writeDocument(String id, Object document) throws CouchDbOperationException;

    /**
     * Maps a Java object to JSON and writes it to the database
     *
     * @param uri      the full URI of the document to write
     * @param document the object to write
     */
    void writeDocument(URI uri, Object document) throws CouchDbOperationException;

    /**
     * Maps a Java object to JSON and writes it to the database, replacing any
     * template variables in the URL which have the same keys as message headers
     * by their corresponding values taken from the <tt>headers</tt> argument.
     *
     * @param id       the id of the document to write
     * @param document the object to write
     */
    void writeDocument(String id, Object payload, MessageHeaders headers);

    /**
     * Maps a Java object to JSON and writes it to the database, replacing any
     * template variables in the URL which have the same keys as message headers
     * by their corresponding values taken from the <tt>headers</tt> argument.
     *
     * @param uri      the full URI of the document to write, possibly containing template parts
     * @param document the object to write
     */
    void writeDocument(URI uri, Object document, MessageHeaders headers) throws CouchDbOperationException;

}
