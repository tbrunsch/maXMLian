package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.NamedAttributeMap;
import dd.kms.maxmlian.api.Node;
import dd.kms.maxmlian.api.Notation;

import javax.xml.stream.events.NotationDeclaration;
import java.util.Collections;

class NotationImpl implements Notation
{
	private final NotationDeclaration	notationDeclaration;
	private final Notation				nextSibling;

	NotationImpl(NotationDeclaration notationDeclaration, Notation nextSibling) {
		this.notationDeclaration = notationDeclaration;
		this.nextSibling = nextSibling;
	}

	@Override
	public String getNodeName() {
		return notationDeclaration.getName();
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
	public Iterable<Node> getChildNodes() {
		return Collections.emptyList();
	}

	@Override
	public Node getFirstChild() {
		return null;
	}

	@Override
	public Node getNextSibling() {
		return nextSibling;
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
	public String getPublicId() {
		return notationDeclaration.getPublicId();
	}

	@Override
	public String getSystemId() {
		return notationDeclaration.getSystemId();
	}
}
