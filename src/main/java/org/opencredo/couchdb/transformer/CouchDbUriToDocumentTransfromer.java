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

import java.net.URI;

/**
 * @author Tareq Abedrabbo
 * @since 01/02/2011
 */
public class CouchDbUriToDocumentTransfromer extends AbstractTransformer {

    private final CouchDbDocumentOperations couchDbDocumentOperations;
    private final Class<?> documentType;

    public CouchDbUriToDocumentTransfromer(Class<?> documentType, CouchDbDocumentOperations couchDbDocumentOperations) {
        Assert.notNull(documentType, "documentType cannot be null");
        Assert.notNull(couchDbDocumentOperations, "couchDbDocumentOperations cannot be null");
        this.documentType = documentType;
        this.couchDbDocumentOperations = couchDbDocumentOperations;
    }


    public CouchDbUriToDocumentTransfromer(Class<?> documentType) {
        this(documentType, new CouchDbDocumentTemplate());
    }

    @Override
    protected Object doTransform(Message<?> message) throws Exception {
        Object payload = message.getPayload();
        URI uri = null;
        if (payload instanceof String) {
            uri = new URI((String) payload);
        } else if (payload instanceof URI) {
            uri = (URI) payload;
        } else {
            throw new MessageHandlingException(message, "Cannot transform payload ["
                    + payload + "] to a CouchDB document");
        }

        return couchDbDocumentOperations.readDocument(uri, documentType);
    }
}
