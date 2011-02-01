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

/**
 * @author Tareq Abedrabbo
 * @since 31/01/2011
 */
public interface CouchDbDocumentOperations {

    Object readDocument(String id, Class<?> documentType);

    Object readDocument(URI uri, Class<?> documentType);

    void writeDocument(String id, Object document);

    void writeDocument(URI uri, Object document);
}
