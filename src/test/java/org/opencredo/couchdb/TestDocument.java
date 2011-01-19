package org.opencredo.couchdb;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

/**
 * @since 13/01/2011
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class TestDocument {

    private String message;

    private String timestamp = new Date().toString();

    public TestDocument() {
    }

    public TestDocument(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestDocument document = (TestDocument) o;

        if (message != null ? !message.equals(document.message) : document.message != null) return false;
        if (timestamp != null ? !timestamp.equals(document.timestamp) : document.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TestDocument{" +
                "message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
