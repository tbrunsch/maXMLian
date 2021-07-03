package dd.kms.maxmlian.impl;

import java.util.Collections;
import java.util.Map;

import javax.xml.stream.events.NotationDeclaration;

import dd.kms.maxmlian.api.Attr;
import dd.kms.maxmlian.api.Node;
import dd.kms.maxmlian.api.NodeType;
import dd.kms.maxmlian.api.Notation;

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
	public Iterable<Node> getChildren() {
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

	@Override
	public String getPublicId() {
		return notationDeclaration.getPublicId();
	}

	@Override
	public String getSystemId() {
		return notationDeclaration.getSystemId();
	}
}
