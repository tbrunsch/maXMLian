package dd.kms.maxmlian.impl;

import java.util.Collections;
import java.util.Map;

import javax.xml.stream.events.Attribute;

import dd.kms.maxmlian.api.Attr;
import dd.kms.maxmlian.api.Node;

class AttrImpl implements Attr
{
	private final NodeFactory	nodeFactory;
	Attribute					attribute;
	private AttrImpl			prevSibling;
	private AttrImpl			nextSibling;
	private AttrText			text;

	AttrImpl(NodeFactory nodeFactory) {
		this.nodeFactory = nodeFactory;
	}

	void initializeFromAttribute(Attribute attribute) {
		this.attribute = attribute;
	}

	void setPrevSibling(AttrImpl prevSibling) {
		if (prevSibling != this.prevSibling && this.prevSibling != null && this.prevSibling.getNextSibling() == this) {
			// ensure that the old previous sibling no longer has this attribute as next sibling
			this.prevSibling.setNextSibling(null);
		}
		this.prevSibling = prevSibling;
		if (prevSibling != null && prevSibling.getNextSibling() != this) {
			// ensure that the new previous sibling has this attribute as next sibling
			prevSibling.setNextSibling(this);
		}
	}

	void setNextSibling(AttrImpl nextSibling) {
		if (nextSibling != this.nextSibling && this.nextSibling != null && this.nextSibling.getPreviousSibling() == this) {
			// ensure that the old next sibling no longer has this attribute as previous sibling
			this.nextSibling.setPrevSibling(null);
		}
		this.nextSibling = nextSibling;
		if (nextSibling != null && nextSibling.getPreviousSibling() != this) {
			// ensure that the new next sibling has this attribute as previous sibling
			nextSibling.setPrevSibling(this);
		}
	}

	@Override
	public String getName() {
		return ImplUtils.getQualifiedName(attribute.getName());
	}

	@Override
	public boolean getSpecified() {
		return attribute.isSpecified();
	}

	@Override
	public String getValue() {
		return attribute.getValue();
	}

	@Override
	public boolean isId() {
		return getValue().equalsIgnoreCase("id");
	}

	@Override
	public String getNodeName() {
		return getName();
	}

	@Override
	public String getNodeValue() {
		return getValue();
	}

	@Override
	public Node getParentNode() {
		return null;
	}

	@Override
	public Iterable<Node> getChildNodes() {
		Node child = getFirstChild();
		return Collections.singletonList(child);
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

	Attr getPreviousSibling() {
		return prevSibling;
	}

	@Override
	public Attr getNextSibling() {
		return nextSibling;
	}

	@Override
	public Map<String, Attr> getAttributes() {
		return Collections.emptyMap();
	}

	@Override
	public String getNamespaceURI() {
		return ImplUtils.emptyToNull(attribute.getName().getNamespaceURI());
	}

	@Override
	public String getPrefix() {
		return ImplUtils.emptyToNull(attribute.getName().getPrefix());
	}

	@Override
	public String getLocalName() {
		return attribute.getName().getLocalPart();
	}

	@Override
	public String getTextContent() {
		return null;
	}
}
