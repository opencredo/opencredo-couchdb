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
