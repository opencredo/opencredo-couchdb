package org.opencredo.couchdb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.integration.Message;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.integration.json.JsonOutboundMessageMapper;
import org.springframework.integration.mapping.OutboundMessageMapper;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.ErrorHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * A message handler that PUTs a CouchDB document for every message.
 * The id of the sent document is by defualt that of the Spring Integration message, but a custom id strategy can be provided.
 * CouchDbSendingMessageHandler relies on RestTemplate to communicate with CouchDB instances. By default a plain RestTemplate
 * is created but a custom can be injected using the appropriate constructor.
 *
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 11/01/2011
 */
public class CouchDbSendingMessageHandler extends AbstractMessageHandler {

    protected transient final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final ExpressionParser expressionParser = new SpelExpressionParser();

    private final RestTemplate restTemplate;
    private final String databaseUrl;
    private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
    private Expression documentIdExpression;


    public CouchDbSendingMessageHandler(String databaseUrl, RestTemplate restTemplate) {
        Assert.hasText(databaseUrl, "databaseUrl cannot be empty");
        Assert.notNull(restTemplate, "restTemplate cannot be null");
        this.databaseUrl = CouchDbUtils.addId(databaseUrl);
        this.restTemplate = restTemplate;
    }

    public CouchDbSendingMessageHandler(String databaseUrl) {
        this(databaseUrl, new RestTemplate());
    }


    @Override
    public void onInit() {
        BeanFactory beanFactory = this.getBeanFactory();
        if (beanFactory != null) {
            this.evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        ConversionService conversionService = this.getConversionService();
        if (conversionService != null) {
            this.evaluationContext.setTypeConverter(new StandardTypeConverter(conversionService));
        }
    }


    @Override
    protected final void handleMessageInternal(Message<?> message) throws Exception {
        String documentId = createDocumentId(message);
        HttpEntity<?> httpEntity = createHttpEntity(message);
        restTemplate.put(databaseUrl, httpEntity, documentId);
    }

    private HttpEntity<?> createHttpEntity(Message<?> message) {
        Object payload = message.getPayload();

        if (payload instanceof HttpEntity) {
            HttpEntity httpEntity = (HttpEntity) payload;
            Assert.isTrue(httpEntity.getHeaders().getContentType().equals(MediaType.APPLICATION_JSON),
                    "HttpEntity payload with non application/json content type found.");
            return httpEntity;
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> httpEntity = new HttpEntity<Object>(payload, httpHeaders);

        return httpEntity;
    }

    private String createDocumentId(Message<?> message) {
        String documentId;
        if (documentIdExpression == null) {
            documentId = message.getHeaders().getId().toString();
        } else {
            documentId = documentIdExpression.getValue(evaluationContext, message, String.class);
        }
        logger.debug("created document id [{}]", documentId);
        return documentId;
    }

    public void setDocumentIdExpression(String documentIdExpression) {
        this.documentIdExpression = expressionParser.parseExpression(documentIdExpression);
    }
}
