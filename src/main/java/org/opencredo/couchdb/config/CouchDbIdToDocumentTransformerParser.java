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

import org.opencredo.couchdb.CouchDbIdToDocumentTransformer;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractTransformerParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import static org.opencredo.couchdb.config.CouchDbAdapterParserUtils.COUCHDB_DATABASE_URL_ATTRIBUTE;
import static org.opencredo.couchdb.config.CouchDbAdapterParserUtils.COUCHDB_DOCUMENT_TYPE;
import static org.opencredo.couchdb.config.CouchDbAdapterParserUtils.COUCHDB_REST_OPERATIONS_ATTRIBUTE;

/**
 * @author Tareq Abedrabbo
 * @since 25/01/2011
 */
public class CouchDbIdToDocumentTransformerParser extends AbstractTransformerParser {
    @Override
    protected String getTransformerClassName() {
        return CouchDbIdToDocumentTransformer.class.getName();
    }

    @Override
    protected void parseTransformer(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
        String databaseUrl = element.getAttribute(COUCHDB_DATABASE_URL_ATTRIBUTE);
        String documentType = element.getAttribute(COUCHDB_DOCUMENT_TYPE);
        String restOperations = element.getAttribute(COUCHDB_REST_OPERATIONS_ATTRIBUTE);

        if (!StringUtils.hasText(databaseUrl)) {
            parserContext.getReaderContext().error("The '" + COUCHDB_DATABASE_URL_ATTRIBUTE + "' is mandatory.", parserContext.extractSource(element));
        }

        if (!StringUtils.hasText(documentType)) {
            parserContext.getReaderContext().error("The '" + COUCHDB_DOCUMENT_TYPE + "' is mandatory.", parserContext.extractSource(element));
        }

        builder.addConstructorArgValue(databaseUrl).addConstructorArgValue(documentType);

        if (StringUtils.hasText(restOperations)) {
            builder.addConstructorArgReference(restOperations);
        }

    }
}
