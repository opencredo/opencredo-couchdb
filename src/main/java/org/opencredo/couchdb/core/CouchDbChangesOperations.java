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

import java.util.List;

/**
 * CouchDB operations that polls for changes using the changes API.
 *
 * @author Tareq Abedrabbo
 * @since 24/01/2011
 */
public interface CouchDbChangesOperations {

    /**
     * Polls the database for latest changes.
     * @return the list of changes or an empty list if no changes are found
     */
    List<ChangedDocument> pollForChanges() throws CouchDbOperationException;
}
