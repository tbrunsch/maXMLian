import java.util.Collections;
import java.util.Map;

import javax.xml.stream.events.Attribute;

class AttrImpl implements Attr
{
	private Attribute	attribute;
	private AttrImpl	prevSibling;
	private AttrImpl	nextSibling;

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
		return XmlUtils.getQualifiedName(attribute.getName());
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
	public NodeType getNodeType() {
		return NodeType.ATTRIBUTE;
	}

	@Override
	public Iterable<Node> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public Node getFirstChild() {
		return null;
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
		return attribute.getName().getNamespaceURI();
	}

	@Override
	public String getPrefix() {
		return attribute.getName().getPrefix();
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
