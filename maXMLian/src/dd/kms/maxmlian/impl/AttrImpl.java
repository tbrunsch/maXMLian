package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Attr;
import dd.kms.maxmlian.api.Element;
import dd.kms.maxmlian.api.NamedAttributeMap;
import dd.kms.maxmlian.api.Node;

class AttrImpl implements Attr
{
	private final boolean	namespaceAware;

	private String			namespaceUri;
	private String			localName;
	private String			prefix;
	private String			value;
	private String			type;
	private AttrImpl		prevSibling;
	private AttrImpl		nextSibling;
	private AttrText		text;

	AttrImpl(boolean namespaceAware) {
		this.namespaceAware = namespaceAware;
	}

	void initialize(String namespaceUri, String localName, String prefix, String value, String type) {
		this.namespaceUri = ImplUtils.emptyToNull(namespaceUri);
		this.localName = localName;
		this.prefix = ImplUtils.emptyToNull(prefix);
		this.value = value;
		this.type = type;
	}

	void setPrevSibling(AttrImpl prevSibling) {
		if (prevSibling != this.prevSibling && this.prevSibling != null && this.prevSibling.getNextSibling() == this) {
			// ensure that the old previous sibling no longer has this attribute as next sibling
			this.prevSibling.nextSibling = null;
		}
		this.prevSibling = prevSibling;
		if (prevSibling != null && prevSibling.getNextSibling() != this) {
			// ensure that the new previous sibling has this attribute as next sibling
			prevSibling.nextSibling = this;
		}
	}

	@Override
	public String getName() {
		return ImplUtils.getQualifiedName(localName, prefix);
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public boolean isId() {
		return "id".equalsIgnoreCase(value);
	}

	@Override
	public String getNodeName() {
		return getName();
	}

	@Override
	public String getNodeValue() {
		return value;
	}

	@Override
	public Node getParentNode() {
		return null;
	}

	@Override
	public Node getFirstChild() {
		if (text == null) {
			text = new AttrText();
		}
		text.setParentNode(this);
		text.setData(getValue());
		return text;
	}

	@Override
	public Attr getNextSibling() {
		return nextSibling;
	}

	@Override
	public Element getFirstChildElement() {
		return null;
	}

	@Override
	public Element getNextSiblingElement() {
		return null;
	}

	@Override
	public NamedAttributeMap getAttributes() {
		return EmptyNamedAttributeMap.ATTRIBUTE_MAP;
	}

	@Override
	public String getNamespaceURI() {
		return namespaceAware ? namespaceUri : null;
	}

	@Override
	public String getPrefix() {
		return namespaceAware ? prefix : null;
	}

	@Override
	public String getLocalName() {
		return namespaceAware ? localName : null;
	}

	@Override
	public String getTextContent() {
		return null;
	}
}
