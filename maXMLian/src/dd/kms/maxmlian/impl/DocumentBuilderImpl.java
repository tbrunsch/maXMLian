package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

class DocumentBuilderImpl implements DocumentBuilder
{
	private final XMLInputFactory	xmlInputFactory;
	private final boolean			reuseInstances;

	DocumentBuilderImpl(XMLInputFactory xmlInputFactory, boolean reuseInstances) {
		this.xmlInputFactory = xmlInputFactory;
		this.reuseInstances = reuseInstances;
	}

	@Override
	public Document parse(InputStream is) throws XmlException {
		XMLStreamReader reader;
		try {
			reader = xmlInputFactory.createXMLStreamReader(is);
		} catch (XMLStreamException e) {
			throw new XmlException("Creating XML stream reader failed: " + e, e);
		}
		ExtendedXmlStreamReader streamReader = new ExtendedXmlStreamReader(reader);
		boolean namespaceAware = Boolean.TRUE.equals(xmlInputFactory.getProperty(XMLInputFactory.IS_NAMESPACE_AWARE));
		NodeFactory nodeFactory = new NodeFactory(streamReader, reuseInstances, namespaceAware);
		Node child;
		try {
			child = nodeFactory.readFirstChild();
		} catch (XMLStreamException e) {
			throw new XmlException("Cannot read first XML node: " + e, e);
		}
		if (child.getNodeType() == NodeType.DOCUMENT) {
			return (Document) child;
		}
		throw new XmlException("Unable to find document node in XML stream");
	}
}
