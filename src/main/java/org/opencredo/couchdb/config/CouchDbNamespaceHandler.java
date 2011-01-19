package org.opencredo.couchdb.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author Tareq Abedrabbo (tareq.abedrabbo@opencredo.com)
 * @since 17/01/2011
 */
public class CouchDbNamespaceHandler extends NamespaceHandlerSupport {

    public void init() {
        registerBeanDefinitionParser("outbound-channel-adapter", new CouchDbOutboundChannelAdapterParser());
    }
}
