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

package org.opencredo.couchdb;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

/**
 * Utility class with operations to manipulate CouchDB URLS
 *
 * @author Tareq Abedrabbo
 * @since 13/01/2011
 */
public class CouchDbUtils {

    private CouchDbUtils() {
    }

    /**
     * Adds an id variable to a URL
     * @param url the URL to modify
     * @return the modified URL
     */
    public static String addId(String url) {
        return ensureTrailingSlash(url) + "{id}";
    }


    /**
     * Adds a 'changes since' variable to a URL
     * @param url
     * @return
     */
    public static String addChangesSince(String url) {
        return ensureTrailingSlash(url) + "_changes?since={seq}";
    }

    /**
     * Ensures that a URL ends with a slash.
     * @param url the URL to modify
     * @return the modified URL
     */
    public static String ensureTrailingSlash(String url) {
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url;
    }

    /**
     * Examines the given URL for a user info part (e.g. {@code http://user:password@hostname...}) and
     * returns an array [ username, password ], if it finds credentials. Otherwise, an empty array is
     * returned
     * @param databaseUrl
     * @return an empty or a 2 element String array
    * @throws URISyntaxException 
     */
   public static String[] extractUsernamePassword(String databaseUrl) throws URISyntaxException {
      URI uri = new URI(databaseUrl);
      return extractUsernamePassword(uri);
   }

   public static String[] extractUsernamePassword(URI uri) {
      final String [] empty = {};
      String userinfo = uri.getUserInfo();
      if(userinfo == null)
         return empty;
      return userinfo.split(":");
   }

}
