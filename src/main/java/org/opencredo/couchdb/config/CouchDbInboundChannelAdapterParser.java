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

import org.opencredo.couchdb.inbound.CouchDbChangesPollingMessageSource;
import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.integration.config.xml.AbstractPollingInboundChannelAdapterParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import static org.opencredo.couchdb.config.CouchDbAdapterParserUtils.COUCHDB_CHANGES_OPERATIONS_ATTRIBUTE;
import static org.opencredo.couchdb.config.CouchDbAdapterParserUtils.COUCHDB_DATABASE_URL_ATTRIBUTE;

/**
 * @author Tareq Abedrabbo
 * @since 25/01/2011
 */
public class CouchDbInboundChannelAdapterParser extends AbstractPollingInboundChannelAdapterParser {
    @Override
    protected BeanMetadataElement parseSource(Element element, ParserContext parserContext) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.
                genericBeanDefinition(CouchDbChangesPollingMessageSource.class);

        String databaseUrl = element.getAttribute(COUCHDB_DATABASE_URL_ATTRIBUTE);
        String changesOperations = element.getAttribute(COUCHDB_CHANGES_OPERATIONS_ATTRIBUTE);

        if (StringUtils.hasText(databaseUrl)) {
            if (StringUtils.hasText(changesOperations)) {
                parserContext.getReaderContext().error(
                        "At most one of '" + COUCHDB_DATABASE_URL_ATTRIBUTE + "' and '" +
                                COUCHDB_CHANGES_OPERATIONS_ATTRIBUTE + "' may be provided.", element);
            } else {
                builder.addConstructorArgValue(databaseUrl);
            }
        } else if (StringUtils.hasText(changesOperations)) {
            builder.addConstructorArgReference(changesOperations);
        } else {
            parserContext.getReaderContext().error(
                    "Either '" + COUCHDB_DATABASE_URL_ATTRIBUTE + "' or '" +
                            COUCHDB_CHANGES_OPERATIONS_ATTRIBUTE + "' must be provided.", element);
        }

        String beanName = BeanDefinitionReaderUtils.registerWithGeneratedName(
                builder.getBeanDefinition(), parserContext.getRegistry());
        return new RuntimeBeanReference(beanName);
    }
}
