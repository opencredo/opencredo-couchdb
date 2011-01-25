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
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Namespace handler for the CouchDB namespace.
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 17/01/2011
 */
public class CouchDbNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("outbound-channel-adapter", new CouchDbOutboundChannelAdapterParser());
        registerBeanDefinitionParser("id-to-document-transformer", new CouchDbIdToDocumentTransformerParser());
    }
}
