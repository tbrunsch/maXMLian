package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Attr;
import dd.kms.maxmlian.api.Element;
import dd.kms.maxmlian.api.Node;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import java.util.Collections;
import java.util.Map;

class ElementImpl extends NodeImpl implements Element
{
	private static final String	PARSE_ATTRIBUTES_ERROR	= "Cannot parse attributes when the XML reader has already parsed beyond the start position of that element";

	private String				namespaceUri;
	private String				localName;
	private String				prefix;
	private Map<String, Attr>	attributesByQName;

	ElementImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initialize(String namespaceUri, String localName, String prefix) {
		super.initialize();
		this.namespaceUri = ImplUtils.emptyToNull(namespaceUri);
		this.localName = localName;
		this.prefix = ImplUtils.emptyToNull(prefix);
		this.attributesByQName = null;
	}

	private void initializeAttributeMap() {
		if (attributesByQName != null) {
			return;
		}
		resetReaderPosition(PARSE_ATTRIBUTES_ERROR);
		XMLStreamReader reader = getReader();

		attributesByQName = createAttributesByQNameMap();
		AttrImpl prevAttr = null;
		// TODO: Make namespace awareness configurable; (Boolean)streamReader.getProperty("javax.xml.stream.isNamespaceAware")
		boolean namespaceAware = true;
		if (namespaceAware) {
			prevAttr = addNamespaceAttributes(reader, prevAttr);
			// TODO: setNamespaceContext(); // see XMLEventAllocatorImpl.getXMLEvent()
		}
		addAttributes(reader, prevAttr);
	}

	private AttrImpl addNamespaceAttributes(XMLStreamReader reader, AttrImpl prevAttr) {
		int numNamespaces = reader.getNamespaceCount();
		for (int i = 0; i < numNamespaces; i++) {
			String uri = reader.getNamespaceURI(i);
			String prefix = reader.getNamespacePrefix(i);
			NamespaceImpl namespace = createNamespace(prefix, uri);
			namespace.setPrevSibling(prevAttr);
			attributesByQName.put(namespace.getName(), namespace);
			prevAttr = namespace;
		}
		return prevAttr;
	}

	private AttrImpl addAttributes(XMLStreamReader reader, AttrImpl prevAttr) {
		int numAttributes = reader.getAttributeCount();
		for (int i = 0; i < numAttributes; i++) {
			QName name = reader.getAttributeName(i);
			String value = reader.getAttributeValue(i);
			String type = reader.getAttributeType(i);
			AttrImpl attr = createAttribute(name.getNamespaceURI(), name.getLocalPart(), name.getPrefix(), value, type);
			attr.setPrevSibling(prevAttr);
			attributesByQName.put(attr.getName(), attr);
			prevAttr = attr;
		}
		return prevAttr;
	}

	@Override
	public String getTagName() {
		return getNodeName();
	}

	@Override
	public String getAttribute(String qName) {
		initializeAttributeMap();
		return attributesByQName.get(qName).getValue();
	}

	@Override
	public boolean hasAttribute(String qName) {
		initializeAttributeMap();
		return attributesByQName.containsValue(qName);
	}

	@Override
	public Map<String, Attr> getAttributes() {
		initializeAttributeMap();
		return Collections.unmodifiableMap(attributesByQName);
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
		return namespaceUri;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getLocalName() {
		return localName;
	}

	@Override
	public Iterable<Node> getChildNodes() {
		resetReaderPosition(PARSE_CHILDREN_ERROR);
		return this;
	}
}
