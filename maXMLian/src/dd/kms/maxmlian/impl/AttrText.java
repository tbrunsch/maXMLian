package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.*;

class AttrText implements Text
{
	private Attr	parent;
	private String	data;

	void setData(String data) {
		this.data = data;
	}

	void setParentNode(Attr parent) {
		this.parent = parent;
	}

	@Override
	public boolean isElementContentWhitespace() {
		return false;
	}

	@Override
	public String getData() {
		return data;
	}

	@Override
	public String getNodeName() {
		return "#text";
	}

	@Override
	public String getNodeValue() {
		return data;
	}

	@Override
	public Node getParentNode() {
		return parent;
	}

	@Override
	public Node getFirstChild() {
		return null;
	}

	@Override
	public Node getNextSibling() {
		return null;
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
		return null;
	}

	@Override
	public String getPrefix() {
		return null;
	}

	@Override
	public String getLocalName() {
		return null;
	}

	@Override
	public String getTextContent() {
		return data;
	}
}
