OpenCredo CouchDB Spring Integration Support provides a collection of Spring Integration components that allow you to
integrate your Spring Integration based applications with CouchDB databases.

# Features
- Outbound channel adapter to write documents to a CouchDB database
- Inbound channel adapter to read modified documents through CouchDB's changes API
- Transformers that read CouchDB documents from the database and maps them to Java classes based on the document
id or URI

# Getting Started
The module is available through the Maven central repositories. The following snippet adds the module
to your project.

    <dependency>
        <groupId>org.opencredo.couchdb</groupId>
        <artifactId>couchdb-si-support</artifactId>
        <version>0.5</version>
    </dependency>

## Dependencies
- Spring Integration 2.0.1
- Spring Web MVC 3.0.5
- Jackson 1.7.1

## Components
The following are the main classes in the CouchDB support package. It is more convenient though to use
the configuration elements provided in the CouchDB custom namespace.

### CouchDbSendingMessageHandler
This MessageHandler extracts payloads of incoming messages, and delegates to a CouchDbDocumentOperations to transform
the payload to JSON and send it to a CouchDB instance.

### CouchDbChangesPollingMessageSource
This MessageSource uses a CouchDbChangesOperations to poll a CouchDB database for changes using the changes API.
For every detected change, a new Message is created. The payload is a java.net.URI object representing the URI of the
changed document. You can then transform this URI into a Java object by passing it through CouchDbUriToDocumentTransfromer.
Note that this object is stateful since it internally queues detected changes till it gets polled.

### CouchDbIdToDocumentTransformer
A transformer that reads a CouchDB document from the database based on the id of the document
contained in the payload of the message.

### CouchDbUriToDocumentTransfromer
A transformer that reads a CouchDB document from the database based on the full URI of the document
contained in the payload of the message.

### CouchDB Operations Support
The org.opencredo.couchdb.core contains a number of classes that manage the low-level communication with CouchDB
databases using the familiar template pattern.
Currently, CouchDbDocumentTemplate and CouchDbChangesTemplate classes are provided
to enable reading and writing documents, and polling CouchDB for changes through the changes API.
Under the hood, these templates rely on the RestTemplate class and its conversion capabilities to manage Restful calls
and mapping object between JSON and Java.
All of provided templates accept a custom RestOperations object if you need to provide your own.

## Namespace Support
To make the CouchDB namespace available, add the following declarations to your application context
configuration file.

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
        database-url="http://127.0.0.1:5984/si_couchdb_test/"/>

By the default the id of the outgoing CouchDB document is the taken from the id of the Spring Integration message
but you can customise how the id is generated with a SpeL expression.

    <si-couchdb:outbound-channel-adapter id="couchdb" channel="input"
        database-url="http://127.0.0.1:5984/si_couchdb_test/"
        document-id-expression="T(java.util.UUID).randomUUID().toString()"/>

### Inbound Channel Adapter
The inbound channel adapter polls CouchDB for changes through its changes API. For every changed document, a message
containing its URI is set to the configured channel.

    <si:channel id="changedDocuments">
        <si:queue capacity="10"/>
    </si:channel>

    <si-couchdb:inbound-channel-adapter channel="changedDocuments"
        database-url="http://127.0.0.1:5984/si_couchdb_test/">
        <si:poller fixed-rate="60000"/>
    </si-couchdb:inbound-channel-adapter>

### Id to Document Transformer
This transformer reads a CouchDB document from the database based on its id, contained in the transformed message.
You need to specify the target class of the transformation. By default Jackson will be used to perform the mapping
from JSON to Java and therefore your class must be properly formed.

    <si:channel id="input"/>

    <si-couchdb:id-to-document-transformer id="documentTransformer"
        database-url="http://127.0.0.1:5984/si_couchdb_test/"
        document-type="org.opencredo.couchdb.DummyDocument"
        input-channel="input"/>

### URI to Document Transformer
This transformer is similar to the previous one but this time it reads and transforms a CouchDB document
using its URI provided as the payload of the message under transformation.

    <si:channel id="input"/>

    <si-couchdb:uri-to-document-transformer id="documentConverter"
        document-type="org.opencredo.couchdb.DummyDocument"
        input-channel="input"/>

# Roadmap
- Inbound channel adapter state store
- Support updating existing documents
- Support different polling modes
- Security

# License
This project is licensed under the Apache License, Version 2.0
