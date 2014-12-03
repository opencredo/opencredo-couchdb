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

package org.opencredo.couchdb.outbound;

import org.opencredo.couchdb.core.CouchDbDocumentOperations;
import org.opencredo.couchdb.core.CouchDbDocumentTemplate;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeConverter;
import org.springframework.messaging.Message;
import org.springframework.integration.handler.AbstractMessageHandler;
import org.springframework.util.Assert;

/**
 * A message handler that creates new CouchDB documents from SI messages.
 * </p>
 * The id of the created documents is by default that of the Spring Integration message,
 * but this can be customized using a SpeL expression.
 *
 * @author Tareq Abedrabbo
 * @since 11/01/2011
 */
public class CouchDbSendingMessageHandler extends AbstractMessageHandler {

    private static final ExpressionParser expressionParser = new SpelExpressionParser();

    private final CouchDbDocumentOperations couchDbDocumentOperations;
    private final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
    private Expression documentIdExpression;


    /**
     * Creates an instance with a custom CouchDbDocumentOperations.
     */
    public CouchDbSendingMessageHandler(CouchDbDocumentOperations couchDbDocumentOperations) {
        Assert.notNull(couchDbDocumentOperations, "couchDbDocumentOperations cannot be null");
        this.couchDbDocumentOperations = couchDbDocumentOperations;
    }

    /**
     * Creates a handler instance with a default database URL
     */
    public CouchDbSendingMessageHandler(String databaseUrl) {
        this(new CouchDbDocumentTemplate(databaseUrl));
    }

    /**
     * Creates a handler instance with a default database URL, user, and password for Basic Authentication
     */
    public CouchDbSendingMessageHandler(String databaseUrl, String username, String password) {
        this(new CouchDbDocumentTemplate(databaseUrl, username, password));
    }

    @Override
    protected void onInit() {
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
        if (logger.isDebugEnabled()) {
            logger.debug("sending message to CouchDB [" + message + "]");
        }
        couchDbDocumentOperations.writeDocument(documentId, message.getPayload(), message.getHeaders());
    }

    private String createDocumentId(Message<?> message) {
        String documentId;
        if (documentIdExpression == null) {
            documentId = message.getHeaders().getId().toString();
        } else {
            documentId = documentIdExpression.getValue(evaluationContext, message, String.class);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("created document id [" +  documentId + "]");
        }
        return documentId;
    }

    /** Sets the Spel expression used to create document ids. If not specified,
     * the default behavior is to use the id of the handled message
     */
    public void setDocumentIdExpression(String documentIdExpression) {
        this.documentIdExpression = expressionParser.parseExpression(documentIdExpression);
    }
}
