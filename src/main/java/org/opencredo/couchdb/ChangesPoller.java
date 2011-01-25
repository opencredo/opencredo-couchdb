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

import java.util.Collection;

/**
 * This component is responsible for polling a CouchDB instance for changes, typically using
 * the changes API.
 *
 * @author Tareq Abedrabbo
 * @since 24/01/2011
 */
public interface ChangesPoller {

    /**
     * Polls a database for changes.
     * @return the collection of changes or an empty collection if no changes are found
     */
    Collection<ChangedDocument> pollForChanges();
}
