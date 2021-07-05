package dd.kms.maxmlian.impl;

import java.util.Collections;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EntityDeclaration;

import dd.kms.maxmlian.api.*;

class EntityImpl implements Entity
{
	private final DocumentType		docType;
	private final EntityDeclaration	entityDeclaration;
	private final Entity			nextSibling;

	EntityImpl(DocumentType docType, EntityDeclaration entityDeclaration, Entity nextSibling) {
		this.docType = docType;
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
		return docType;
	}

	@Override
	public Iterable<Node> getChildNodes() {
		return Collections.emptyList();
	}

	@Override
	public Node getFirstChild() throws XMLStreamException {
		return null;
	}

	@Override
	public Entity getNextSibling() {
		return nextSibling;
	}

	@Override
	public Map<String, Attr> getAttributes() {
		return null;
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
}
