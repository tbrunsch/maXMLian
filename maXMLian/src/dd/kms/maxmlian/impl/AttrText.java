package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Attr;
import dd.kms.maxmlian.api.NamedAttributeMap;
import dd.kms.maxmlian.api.Node;
import dd.kms.maxmlian.api.Text;

import javax.xml.stream.XMLStreamException;
import java.util.Collections;

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
	public Iterable<Node> getChildNodes() {
		return Collections.emptyList();
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
