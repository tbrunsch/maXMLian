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

	DocumentBuilderImpl(XMLInputFactory xmlInputFactory, boolean reuseInstances, DtdSupport dtdSupport) {
		this.xmlInputFactory = xmlInputFactory;
		this.reuseInstances = reuseInstances;
		applyDtdSupport(dtdSupport);
	}

	private void applyDtdSupport(DtdSupport dtdSupport) {
		boolean supportDtds = dtdSupport != DtdSupport.NONE;
		boolean supportExternalDtds = dtdSupport == DtdSupport.INTERNAL_AND_EXTERNAL;
		xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, supportDtds);
		xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, supportExternalDtds);
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
