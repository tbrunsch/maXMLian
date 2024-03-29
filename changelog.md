# Changelog

## v0.2.0

API changes:
* Added factory method `DocumentBuilderFactory.newInstance(XMLInputFactory)` that allows specifying the `XMLInputFactory` that will be used for creating the underlying StAX parser.
* Added method `DocumentBuilderFactory.normalize()` to specify whether adjacent text nodes should be joined or not.
* Renamed method `DocumentBuilderFactory.setNamespaceAware()` to `namespaceAware()`.
* Added method `Node.getTextContentStream()` to obtain the text content of a node as a stream. This can be helpful if the text content is large and stream reading it is sufficient.
* Made `Document` an `AutoCloseable`, i.e., it should now be used with try-with-resources.

Behavioral changes:
* `DocumentBuilderFactory.namespaceAware()` may now throw an `IllegalStateException` if the underlying `XMLInputFactory` does not support this option.
* Adjacent text nodes are not joined anymore by default. You have to call `DocumentBuilderFactory.normalize()` to achieve this.
* DTD (document type definition) support is now disabled by default for security reasons. You have to call `DocumentBuilderFactory.dtdSupport()` to specify to which extent DTDs shall be supported. 

## v0.1.0
First maXMLian release