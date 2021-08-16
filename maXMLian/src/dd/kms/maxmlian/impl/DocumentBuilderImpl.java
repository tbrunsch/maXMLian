package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Document;
import dd.kms.maxmlian.api.DocumentBuilder;
import dd.kms.maxmlian.api.Node;
import dd.kms.maxmlian.api.NodeType;

import javax.xml.stream.*;
import java.io.InputStream;

class DocumentBuilderImpl implements DocumentBuilder
{
	private static final String	PROP_NAMESPACE_AWARE	= "javax.xml.stream.isNamespaceAware";

	private final int		reuseDelay;
	private final boolean	namespaceAware;

	DocumentBuilderImpl(int reuseDelay, boolean namespaceAware) {
		this.reuseDelay = reuseDelay;
		this.namespaceAware = namespaceAware;
	}

	@Override
	public Document parse(InputStream is) throws XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		factory.setProperty(PROP_NAMESPACE_AWARE, namespaceAware);
		XMLStreamReader reader = factory.createXMLStreamReader(is);
		ExtendedXmlStreamReader streamReader = new ExtendedXmlStreamReader(reader);
		NodeFactory nodeFactory = new NodeFactory(streamReader, reuseDelay, namespaceAware);
		Node child = nodeFactory.readFirstChildNode();
		if (child.getNodeType() == NodeType.DOCUMENT) {
			return (Document) child;
		}
		throw new XMLStreamException("Unable to find document node in XML stream");
	}
}
