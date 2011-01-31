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

package org.opencredo.couchdb.config;

/**
 * Utility class for the CouchDB namespace support.
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 17/01/2011
 */
public class CouchDbAdapterParserUtils {

    private CouchDbAdapterParserUtils() {}

    static final String COUCHDB_DATABASE_URL_ATTRIBUTE = "database-url";
    static final String COUCHDB_DOCUMENT_ID_EXPRESSION_ATTRIBUTE = "document-id-expression";
    static final String COUCHDB_DOCUMENT_TYPE_ATTRIBUTE = "document-type";
    static final String COUCHDB_DOCUMENT_OPERATIONS_ATTRIBUTE = "document-operations";
    static final String COUCHDB_CHANGES_OPERATIONS_ATTRIBUTE = "changes-operations";

    static final String COUCHDB_DOCUMENT_ID_EXPRESSION_PROPERTY = "documentIdExpression";

}
