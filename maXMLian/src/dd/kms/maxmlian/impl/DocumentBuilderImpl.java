package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Document;
import dd.kms.maxmlian.api.DocumentBuilder;
import dd.kms.maxmlian.api.Node;
import dd.kms.maxmlian.api.NodeType;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.List;

class DocumentBuilderImpl implements DocumentBuilder
{
	private static final String	PROP_NAMESPACE_AWARE	= "javax.xml.stream.isNamespaceAware";
	private static final String	PROP_VALIDATING			= "javax.xml.stream.isValidating";

	private final boolean	reuseInstances;
	private final boolean	namespaceAware;

	private final List<XMLInputFactoryProvider>	xmlInputFactoryProviders;

	DocumentBuilderImpl(boolean reuseInstances, boolean namespaceAware, List<XMLInputFactoryProvider> xmlInputFactoryProviders) {
		this.reuseInstances = reuseInstances;
		this.namespaceAware = namespaceAware;
		this.xmlInputFactoryProviders = xmlInputFactoryProviders;
	}

	@Override
	public Document parse(InputStream is) throws XMLStreamException {
		XMLInputFactory factory = null;
		for (XMLInputFactoryProvider xmlInputFactoryProvider : xmlInputFactoryProviders) {
			factory = xmlInputFactoryProvider.getXMLInputFactory().orElse(null);
			if (factory != null) {
				break;
			}
		}
		if (factory == null) {
			throw new IllegalStateException("Cannot instantiate an XMLInputFactory");
		}

		factory.setProperty(PROP_NAMESPACE_AWARE, namespaceAware);
		factory.setProperty(PROP_VALIDATING, false);
		XMLStreamReader reader = factory.createXMLStreamReader(is);
		ExtendedXmlStreamReader streamReader = new ExtendedXmlStreamReader(reader);
		NodeFactory nodeFactory = new NodeFactory(streamReader, reuseInstances, namespaceAware);
		Node child = nodeFactory.readFirstChildNode();
		if (child.getNodeType() == NodeType.DOCUMENT) {
			return (Document) child;
		}
		throw new XMLStreamException("Unable to find document node in XML stream");
	}
}
