package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.*;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.List;

class DocumentBuilderImpl implements DocumentBuilder
{
	private static final String	PROP_NAMESPACE_AWARE	= "javax.xml.stream.isNamespaceAware";
	private static final String	PROP_VALIDATING			= "javax.xml.stream.isValidating";
	private static final String	PROP_IS_COALESCING		= "javax.xml.stream.isCoalescing";

	private final boolean	reuseInstances;
	private final boolean	namespaceAware;
	private final boolean	normalize;

	private final List<XMLInputFactoryProvider>	xmlInputFactoryProviders;

	DocumentBuilderImpl(boolean reuseInstances, boolean namespaceAware, boolean normalize, List<XMLInputFactoryProvider> xmlInputFactoryProviders) {
		this.reuseInstances = reuseInstances;
		this.namespaceAware = namespaceAware;
		this.normalize = normalize;
		this.xmlInputFactoryProviders = xmlInputFactoryProviders;
	}

	@Override
	public Document parse(InputStream is) throws XmlException {
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
		factory.setProperty(PROP_IS_COALESCING, normalize);
		XMLStreamReader reader;
		try {
			reader = factory.createXMLStreamReader(is);
		} catch (XMLStreamException e) {
			throw new XmlException("Creating XML stream reader failed: " + e, e);
		}
		ExtendedXmlStreamReader streamReader = new ExtendedXmlStreamReader(reader);
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
