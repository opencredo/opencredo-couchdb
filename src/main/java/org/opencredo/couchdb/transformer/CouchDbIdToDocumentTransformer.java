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

package org.opencredo.couchdb.transformer;

import org.opencredo.couchdb.core.CouchDbDocumentOperations;
import org.opencredo.couchdb.core.CouchDbDocumentTemplate;
import org.springframework.messaging.Message;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * A message transformer that reads a CouchDB document from the database.
 * </p>
 * The payload of the message is expected to contain the id of the message to read.
 * </p>
 * Once the document read, it is mapped to the Java class specified as documentType in the constructor.
 *
 * @author Tareq Abedrabbo
 * @since 21/01/2011
 */
public class CouchDbIdToDocumentTransformer extends AbstractTransformer {

    private final CouchDbDocumentOperations couchDbDocumentOperations;
    private final Class<?> documentType;

    /**
     * Creates an instance with a custom CouchDbDocumentOperations.
     *
     * @param documentType              the target class to map documents to
     * @param couchDbDocumentOperations a custom CouchDbDocumentOperations
     */
    public CouchDbIdToDocumentTransformer(Class<?> documentType, CouchDbDocumentOperations couchDbDocumentOperations) {
        Assert.notNull(documentType, "documentType cannot be null");
        Assert.notNull(couchDbDocumentOperations, "couchDbDocumentOperations cannot be null");
        this.documentType = documentType;
        this.couchDbDocumentOperations = couchDbDocumentOperations;
    }

    /**
     * Creates an instance with a default database URL to connect to
     *
     * @param documentType the target class to map documents to
     * @param databaseUrl  the default database URL
     */
    public CouchDbIdToDocumentTransformer(Class<?> documentType, String databaseUrl) {
        this(documentType, new CouchDbDocumentTemplate(databaseUrl));
    }

    @Override
    protected Object doTransform(Message<?> message) throws Exception {
        Object payload = message.getPayload();
        String id = null;
        if (payload instanceof String) {
            id = (String) payload;
        } else if (payload instanceof UUID) {
            id = payload.toString();
        } else {
            throw new MessageTransformationException(message, "Cannot transform payload ["
                    + payload + "] to a CouchDB document");
        }

        return couchDbDocumentOperations.readDocument(id, documentType);
    }

}
