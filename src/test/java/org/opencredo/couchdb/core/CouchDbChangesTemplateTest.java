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

import org.junit.Before;
import org.junit.Test;
import org.opencredo.couchdb.core.ChangedDocument;
import org.opencredo.couchdb.core.CouchDbChangesTemplate;
import org.springframework.web.client.RestOperations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.opencredo.couchdb.core.CouchDbChangesTemplate.Change;
import static org.opencredo.couchdb.core.CouchDbChangesTemplate.Changes;
import static org.opencredo.couchdb.core.CouchDbChangesTemplate.Revision;


/**
 * @author Tareq Abedrabbo
 * @since 24/01/2011
 */
public class CouchDbChangesTemplateTest {

    private static final int NUMBER_OF_CHANGES = 10;

    private CouchDbChangesTemplate changesTemplate;
    private RestOperations restOperations;

    @Before
    public void setUp() throws Exception {
        restOperations = mock(RestOperations.class);
        changesTemplate = new CouchDbChangesTemplate("test");
        changesTemplate.setRestOperations(restOperations);
    }

    @Test
    public void pollForChanges() throws Exception {
        when(restOperations.getForObject(anyString(), eq(Changes.class), eq(Long.valueOf(0L)))).
                thenReturn(createChanges(NUMBER_OF_CHANGES));

        Collection<ChangedDocument> documents = changesTemplate.pollForChanges();
        assertThat(documents, is(notNullValue()));
        assertThat(documents.size(), equalTo(NUMBER_OF_CHANGES));
    }

    private Changes createChanges(int n) {
        Changes changes = new Changes();
        ArrayList<Change> results = new ArrayList<Change>();
        changes.setResults(results);
        changes.setLast_seq(Long.valueOf(n));
        for (int i = 0; i < n; i++) {
            Change change = new Change();
            change.setSeq(Long.valueOf(i));
            change.setDeleted(false);
            change.setId(UUID.randomUUID().toString());
            Revision revision = new Revision();
            revision.setRev("1-" + UUID.randomUUID());
            change.setChanges(Collections.singletonList(revision));
            results.add(change);
        }
        return changes;
    }
}
