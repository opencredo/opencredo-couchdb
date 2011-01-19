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

import org.opencredo.couchdb.CouchDbSendingMessageHandler;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractOutboundChannelAdapterParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import static org.springframework.integration.config.xml.IntegrationNamespaceUtils.*;
import static org.opencredo.couchdb.config.CouchDbAdapterParserUtils.*;

/**
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 17/01/2011
 */
public class CouchDbOutboundChannelAdapterParser extends AbstractOutboundChannelAdapterParser {

    @Override
    protected AbstractBeanDefinition parseConsumer(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CouchDbSendingMessageHandler.class);
        String databaseUrl = element.getAttribute(COUCHDB_DATABASE_URL_ATTRIBUTE);
        String restTempte = element.getAttribute(COUCHDB_REST_TEMPLATE_ATTRIBUTE);
        String documentIdExpression = element.getAttribute(COUCHDB_DOCUMENT_ID_EXPRESSION_ATTRIBUTE);

        if (!StringUtils.hasText(databaseUrl)) {
            parserContext.getReaderContext().error("The 'database-url' is mandatory.", parserContext.extractSource(element));
        }

        builder.addConstructorArgValue(databaseUrl);

        if (StringUtils.hasText(restTempte)) {
            builder.addConstructorArgReference(restTempte);
        }

        if (StringUtils.hasText(documentIdExpression)) {
            builder.addPropertyValue(COUCHDB_DOCUMENT_ID_EXPRESSION_PROPERTY, documentIdExpression);
        }

        return builder.getBeanDefinition();
    }
}
