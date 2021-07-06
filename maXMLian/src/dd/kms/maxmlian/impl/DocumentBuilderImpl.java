package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Document;
import dd.kms.maxmlian.api.DocumentBuilder;
import dd.kms.maxmlian.api.Node;
import dd.kms.maxmlian.api.NodeType;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

class DocumentBuilderImpl implements DocumentBuilder
{
	@Override
	public Document parse(InputStream is) throws XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = factory.createXMLEventReader(is);
		ExtendedXmlEventReader extendedReader = new ExtendedXmlEventReader(eventReader);
		NodeFactory nodeFactory = new NodeFactory(extendedReader);
		Node child = nodeFactory.readFirstChildNode();
		if (child.getNodeType() == NodeType.DOCUMENT) {
			return (Document) child;
		}
		throw new XMLStreamException("Unable to find document node in XML stream");
	}
}
