package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.*;

import javax.xml.stream.events.EntityDeclaration;

class EntityImpl implements Entity
{
	private final EntityDeclaration	entityDeclaration;
	private final Entity			nextSibling;

	EntityImpl(EntityDeclaration entityDeclaration, Entity nextSibling) {
		this.entityDeclaration = entityDeclaration;
		this.nextSibling = nextSibling;
	}

	@Override
	public String getPublicId() {
		return entityDeclaration.getPublicId();
	}

	@Override
	public String getSystemId() {
		return entityDeclaration.getSystemId();
	}

	@Override
	public String getNotationName() {
		return entityDeclaration.getNotationName();
	}

	@Override
	public String getNodeName() {
		return entityDeclaration.getName();
	}

	@Override
	public String getNodeValue() {
		return null;
	}

	@Override
	public Node getParentNode() {
		return null;
	}

	@Override
	public Node getFirstChild() {
		return null;
	}

	@Override
	public Entity getNextSibling() {
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
		return null;
	}

	@Override
	public StringStream getTextContentStream() {
		return EmptyStringStream.STRING_STREAM;
	}
}
