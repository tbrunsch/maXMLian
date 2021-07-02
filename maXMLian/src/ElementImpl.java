import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;

class ElementImpl extends NodeImpl implements Element
{
	private QName										name;
	private Map<String, Attr>							attributesByQName;
	private Iterator<javax.xml.stream.events.Namespace>	namespaceIterator;
	private Iterator<javax.xml.stream.events.Attribute>	attributeIterator;

	ElementImpl(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initializeFromStartElement(StartElement startElement) {
		super.initializePosition();
		this.name = startElement.getName();
		this.namespaceIterator = startElement.getNamespaces();
		this.attributeIterator = startElement.getAttributes();
	}

	private void initializeAttributeMap() {
		if (attributesByQName != null) {
			return;
		}
		if (namespaceIterator == null) {
			throw new IllegalStateException("Namespace iterator of element '" + getTagName() + "' is null");
		}
		if (attributeIterator == null) {
			throw new IllegalStateException("Attribute iterator of element '" + getTagName() + "' is null");
		}
		// TODO: Possibly reuse old maps
		attributesByQName = new LinkedHashMap<>();
		AttrImpl prevAttr = null;

		while (namespaceIterator.hasNext()) {
			javax.xml.stream.events.Namespace ns = namespaceIterator.next();
			NamespaceImpl namespace = createNamespace(ns);
			if (prevAttr != null) {
				prevAttr.setNextSibling(namespace);
			}
			attributesByQName.put(namespace.getName(), namespace);
			prevAttr = namespace;
		}

		while (attributeIterator.hasNext()) {
			javax.xml.stream.events.Attribute attribute = attributeIterator.next();
			AttrImpl attr = createAttribute(attribute);
			if (prevAttr != null) {
				prevAttr.setNextSibling(attr);
			}
			attributesByQName.put(attr.getName(), attr);
			prevAttr = attr;
		}
		namespaceIterator = null;
		attributeIterator = null;
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
		return XmlUtils.getQualifiedName(name);
	}

	@Override
	public String getNodeValue() {
		return null;
	}

	@Override
	public NodeType getNodeType() {
		return NodeType.ELEMENT;
	}

	@Override
	public String getNamespaceURI() {
		return name.getNamespaceURI();
	}

	@Override
	public String getPrefix() {
		return name.getPrefix();
	}

	@Override
	public String getLocalName() {
		return name.getLocalPart();
	}
}
