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
import org.springframework.integration.Message;
import org.springframework.integration.MessageHandlingException;
import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * A message transformer that reads a CouchDB document from the database.
 * </p>
 * The payload of the message is expected to contain the id of the message to read.
 * The class type to transform to is specified through the the documentType property.
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 21/01/2011
 */
public class CouchDbIdToDocumentTransformer extends AbstractTransformer{

    private final CouchDbDocumentOperations couchDbDocumentOperations;
    private final Class<?> documentType;

    public CouchDbIdToDocumentTransformer(Class<?> documentType, CouchDbDocumentOperations couchDbDocumentOperations) {
        Assert.notNull(documentType, "documentType cannot be null");
        Assert.notNull(couchDbDocumentOperations, "couchDbDocumentOperations cannot be null");
        this.documentType = documentType;
        this.couchDbDocumentOperations = couchDbDocumentOperations;
    }

    public CouchDbIdToDocumentTransformer(Class<?> documentType, String databaseUrl) {
        this(documentType, new CouchDbDocumentTemplate(databaseUrl));
    }

    @Override
    protected Object doTransform(Message<?> message) throws Exception {
        Object payload = message.getPayload();
        String id = null;
        if (payload instanceof String) {
            id = (String) payload;
        }
        else
        if (payload instanceof UUID) {
            id = ((UUID) payload).toString();
        }
        else {
            throw new MessageHandlingException(message, "Cannot handle transform payload ["
                    + payload + "] to a CouchDB id.");
        }

        return couchDbDocumentOperations.readDocument(id, documentType);
    }

}
