package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Document;
import dd.kms.maxmlian.api.DocumentBuilder;
import dd.kms.maxmlian.api.Node;
import dd.kms.maxmlian.api.NodeType;

import javax.xml.stream.*;
import java.io.InputStream;

class DocumentBuilderImpl implements DocumentBuilder
{
	private final int	reuseDelay;

	DocumentBuilderImpl(int reuseDelay) {
		this.reuseDelay = reuseDelay;
	}

	@Override
	public Document parse(InputStream is) throws XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(is);
		ExtendedXmlStreamReader streamReader = new ExtendedXmlStreamReader(reader);
		NodeFactory nodeFactory = new NodeFactory(streamReader, reuseDelay);
		Node child = nodeFactory.readFirstChildNode();
		if (child.getNodeType() == NodeType.DOCUMENT) {
			return (Document) child;
		}
		throw new XMLStreamException("Unable to find document node in XML stream");
	}
}
