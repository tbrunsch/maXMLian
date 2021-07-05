package dd.kms.maxmlian.impl;

import java.util.Collections;
import java.util.Map;

import javax.xml.stream.events.NotationDeclaration;

import dd.kms.maxmlian.api.*;

class NotationImpl implements Notation
{
	private final DocumentType			docType;
	private final NotationDeclaration	notationDeclaration;
	private final Notation				nextSibling;

	NotationImpl(DocumentType docType, NotationDeclaration notationDeclaration, Notation nextSibling) {
		this.docType = docType;
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
		return docType;
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
