package org.opencredo.couchdb;

/**
 * @since 13/01/2011
 */
public class CouchDbUtils {

    private CouchDbUtils() {
    }

    public static String addId(String url) {
        return ensureTrailingSlash(url) + "{id}";
    }

    public static String addChangesSince(String url) {
        return ensureTrailingSlash(url) + "_changes?since={seq}";
    }

    private static String ensureTrailingSlash(String url) {
        if (!url.endsWith("/")) {
            url += "/";
        }
        return url;
    }



}
