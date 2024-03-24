package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Attr;
import dd.kms.maxmlian.api.Element;
import dd.kms.maxmlian.api.NamedAttributeMap;
import dd.kms.maxmlian.api.XmlStateException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

class ElementImpl extends NodeImpl implements Element
{
	private static final String	PARSE_ATTRIBUTES_ERROR	= "Cannot parse attributes when the XML reader has already parsed beyond the start position of that element";

	private String				namespaceUri;
	private String				localName;
	private String				prefix;
	private NamedAttributeMap	attributeMap;

	ElementImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initialize(String namespaceUri, String localName, String prefix) {
		super.initialize();
		this.namespaceUri = ImplUtils.emptyToNull(namespaceUri);
		this.localName = localName;
		this.prefix = ImplUtils.emptyToNull(prefix);
		this.attributeMap = null;
	}

	private void initializeAttributeMap() throws XmlStateException {
		if (attributeMap != null) {
			return;
		}
		resetReaderPosition(PARSE_ATTRIBUTES_ERROR);
		XMLStreamReader reader = getReader();

		int numNamespaces = reader.getNamespaceCount();
		int numAttributes = reader.getAttributeCount();

		if (numNamespaces == 0 && numAttributes == 0) {
			attributeMap = EmptyNamedAttributeMap.ATTRIBUTE_MAP;
			return;
		}

		NamedAttributeMapImpl attributeMap = createNamedAttributeMap();

		/*
		 * The Aalto XML parser always creates namespaces instead of attributes.
		 * Hence, we must parse namespaces, even if namespace awareness is deactivated.
		 */
		addNamespaceAttributes(attributeMap, reader, numNamespaces);
		addAttributes(attributeMap, reader, numAttributes);

		this.attributeMap = attributeMap;
	}

	private void addNamespaceAttributes(NamedAttributeMapImpl attributeMap, XMLStreamReader reader, int numNamespaces) {
		for (int i = 0; i < numNamespaces; i++) {
			String uri = reader.getNamespaceURI(i);
			String prefix = ImplUtils.emptyToNull(reader.getNamespacePrefix(i));
			NamespaceImpl namespace = createNamespace(prefix, uri);
			attributeMap.add(namespace);
		}
	}

	private void addAttributes(NamedAttributeMapImpl attributeMap, XMLStreamReader reader, int numAttributes) {
		for (int i = 0; i < numAttributes; i++) {
			QName name = reader.getAttributeName(i);
			String value = reader.getAttributeValue(i);
			String type = reader.getAttributeType(i);
			AttrImpl attr = createAttribute(name.getNamespaceURI(), name.getLocalPart(), name.getPrefix(), value, type);
			attributeMap.add(attr);
		}
	}

	@Override
	public String getTagName() {
		return getNodeName();
	}

	@Override
	public String getAttribute(String qName) throws XmlStateException {
		initializeAttributeMap();
		Attr attr = attributeMap.get(qName);
		return attr != null ? attr.getValue() : null;
	}

	@Override
	public boolean hasAttribute(String qName) throws XmlStateException {
		initializeAttributeMap();
		Attr attr = attributeMap.get(qName);
		return attr == null;
	}

	@Override
	public NamedAttributeMap getAttributes() throws XmlStateException {
		initializeAttributeMap();
		return attributeMap;
	}

	@Override
	public String getNodeName() {
		return ImplUtils.getQualifiedName(localName, prefix);
	}

	@Override
	public String getNodeValue() {
		return null;
	}

	@Override
	public String getNamespaceURI() {
		return isNamespaceAware() ? namespaceUri : null;
	}

	@Override
	public String getPrefix() {
		return isNamespaceAware() ? prefix : null;
	}

	@Override
	public String getLocalName() {
		return isNamespaceAware() ? localName : null;
	}
}
