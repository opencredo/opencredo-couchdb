OpenCredo CouchDB Spring Integration Support provides a collection of Spring Integration components that allows you to
integrate your Spring Integration applications with CouchDB databases.

# Features
- An outbound channel adapter to write documents to a CouchDB database
- An inbound channel adapter to read documents from a CouchDB database
- A transformer that reads and transforms URIs representing CouchDB documents into Java classes
# Getting Started
## Dependencies
- Spring Integration 2.0.1
- Spring Web MVC 3.0.5
- Jackson 1.7.1
## Using with Maven
    <dependency>
        <groupId>org.opencredo.couchdb</groupId>
        <artifactId>couchdb-si-support</artifactId>
        <version>0.5</version>
    </dependency>
## Components
### CouchDbSendingMessageHandler
A MessageHandler that extracts payloads of incoming messages, transform them to
### CouchDbChangesPollingMessageSource
### CouchDbIdToDocumentTransformer
### CouchDB Operations Support
The org.opencredo.couchdb.core contains classes that manages communications with CouchDB databases using RestTemplate
and Jackson. All of these components allow you to specify a custom RestOperations. 
CouchDbDocumentOperations and CouchDbChangesOperations

## Namespace Support

    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:si="http://www.springframework.org/schema/integration"
           xmlns:si-couchdb="http://www.opencredo.com/schema/couchdb/integration"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans.xsd
		   http://www.opencredo.com/schema/couchdb/integration
		   http://www.opencredo.com/schema/couchdb/integration/opencredo-integration-couchdb-1.0.xsd
		   http://www.springframework.org/schema/integration
		   http://www.springframework.org/schema/integration/spring-integration-2.0.xsd">

### Outbound Channel Adapter

    <si:channel id="input"/>

    <si-couchdb:outbound-channel-adapter id="couchdb" channel="input"
        database-url="http://127.0.0.1:5984/si_couchdb_test/" />

By the default the id of the CouchDB document is that of the handled message but you can alternatively customise that
with a SpeL expression.

    <si-couchdb:outbound-channel-adapter id="couchdb" channel="input"
        database-url="http://127.0.0.1:5984/si_couchdb_test/"
        document-id-expression="T(java.util.UUID).randomUUID().toString()"/>

### Inbound Channel Adapter

### Id to Document Transformer

# Roadmap
- Inbound channel adapter state store
- Support updating existing documents
- Support different polling modes