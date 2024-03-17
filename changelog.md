# Changelog

## v0.2.0

API changes:
* `DocumentBuilderFactory.newInstance()` now allows specifying a prioritized list of `XMLInputFactory`s that is considered when selecting the underlying StAX parser.
* Added method `DocumentBuilderFactory.normalize()` to specify whether adjacent text nodes should be joined or not.
* Renamed method `DocumentBuilderFactory.setNamespaceAware()` to `namespaceAware()`.

Behavioral changes:
* `DocumentBuilderFactory.namespaceAware()` may now throw an `IllegalStateException` if the underlying `XMLInputFactory` does not support this option.
* Adjacent text nodes are not joined anymore by default. You have to call `DocumentBuilderFactory.normalize()` to achieve this.

## v0.1.0
First maXMLian release