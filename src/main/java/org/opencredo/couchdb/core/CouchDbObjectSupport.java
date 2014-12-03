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

package org.opencredo.couchdb.core;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencredo.couchdb.BasicAuthRestTemplate;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;

/**
 * Base class for classes that need to communicate with CouchDB.
 * @author Tareq Abedrabbo
 * @since 02/02/2011
 */
public abstract class CouchDbObjectSupport {

    protected final Log logger = LogFactory.getLog(this.getClass());

    protected RestOperations restOperations;

    protected CouchDbObjectSupport() {
       restOperations = new BasicAuthRestTemplate(100);
    }

    protected CouchDbObjectSupport(String username, String password, String url) {
       restOperations = new BasicAuthRestTemplate(username, password, url, 100);
    }

    public CouchDbObjectSupport(String defaultDatabaseUrl) {
        restOperations = new BasicAuthRestTemplate(defaultDatabaseUrl);
    }

    public void setRestOperations(RestOperations restOperations) {
        Assert.notNull(restOperations, "restOperations cannot be null");
        this.restOperations = restOperations;
    }
}
