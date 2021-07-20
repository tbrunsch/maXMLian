package dd.kms.maxmlian.impl;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import dd.kms.maxmlian.api.*;

// The class is also an Iterable over its own children
abstract class NodeImpl implements Node, Iterable<Node>
{
	private static final String	PARSE_CHILDREN_ERROR	= "Cannot parse children when the XML reader has already parsed beyond the start position of that node";
	private static final String	PARSE_SIBLING_ERROR		= "Cannot parse sibling when the XML reader has already parsed beyond the end of that node";

	private final ExtendedXmlEventReader	eventReader;
	private final NodeFactory				nodeFactory;

	// Positional information
	private int								initialDepth;
	private long							initialPosition;
	private long                          	initialNodeCounterDepth;

	private Node							parent;
	private final StringBuilder				stringBuilder		= new StringBuilder();

	NodeImpl(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		this.eventReader = eventReader;
		this.nodeFactory = nodeFactory;
	}

	void initialize() {
		initialDepth = eventReader.getDepth();
		initialPosition = eventReader.position();
		initialNodeCounterDepth = eventReader.getNodeCounter(initialDepth);
		parent = null;
	}

	void setParentNode(Node parent) {
		this.parent = parent;
	}

	@Override
	public String getNodeValue() {
		return null;
	}

	@Override
	public Node getParentNode() {
		return parent;
	}

	@Override
	public Iterable<Node> getChildNodes() throws XMLStreamException {
		NodeType nodeType = getNodeType();
		if (nodeType == NodeType.ELEMENT || nodeType == NodeType.DOCUMENT) {
			if (!eventReader.position(initialPosition)) {
				throw createStateException(PARSE_CHILDREN_ERROR);
			}
			return this;
		}
		return Collections.emptyList();
	}

	@Override
	public Iterator<Node> iterator() {
		try {
			if (!eventReader.position(initialPosition)) {
				throw createStateException(PARSE_CHILDREN_ERROR);
			}
		} catch (XMLStreamException e) {
			throw createStateException(PARSE_CHILDREN_ERROR, e);
		}
		ChildIterator childIterator = nodeFactory.createChildIterator();
		childIterator.setParentNode(this);
		return childIterator;
	}

	@Override
	public Node getFirstChild() {
		Iterator<Node> childIterator = iterator();
		return childIterator.hasNext() ? childIterator.next() : null;
	}

	@Override
	public Node getNextSibling() {
		if (eventReader.getDepth() < initialDepth || eventReader.getNodeCounter(initialDepth) != initialNodeCounterDepth) {
			resetReaderPosition(PARSE_SIBLING_ERROR);
		}
		try {
			return nodeFactory.getNextSibling(initialDepth);
		} catch (XMLStreamException e) {
			throw new XmlException("Cannot read next sibling from XML: " + e.getMessage(), e);
		}
	}

	@Override
	public Map<String, Attr> getAttributes() {
		return Collections.emptyMap();
	}

	private void resetReaderPosition(String errorMessage) {
		try {
			if (!eventReader.position(initialPosition)) {
				throw createStateException(errorMessage);
			}
		} catch (XMLStreamException e) {
			throw createStateException(errorMessage, e);
		}
	}

	@Override
	public final String getTextContent() throws XMLStreamException {
		NodeType nodeType = getNodeType();
		if (nodeType == NodeType.TEXT || nodeType == NodeType.COMMENT) {
			return getNodeValue();
		}
		stringBuilder.setLength(0);
		appendTextContentTo(stringBuilder);
		return stringBuilder.toString();
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

	protected Map<String, Attr> createAttributesByQNameMap() {
		return nodeFactory.createAttributesByQNameMap(initialDepth);
	}

	protected NamespaceImpl createNamespace(javax.xml.stream.events.Namespace namespace) {
		return nodeFactory.createNamespace(namespace, initialDepth);
	}

	protected AttrImpl createAttribute(javax.xml.stream.events.Attribute attribute) {
		return nodeFactory.createAttribute(attribute, initialDepth);
	}

	private void appendTextContentTo(StringBuilder builder) throws XMLStreamException {
		NodeType nodeType = getNodeType();
		if (nodeType == NodeType.TEXT || nodeType == NodeType.COMMENT) {
			builder.append(getNodeValue());
		}
		for (Node child : getChildNodes()) {
			((NodeImpl) child).appendTextContentTo(builder);
		}
	}

	XmlStateException createStateException(String message) {
		return new XmlStateException("dd.kms.maxmlian.api.Node '" + getNodeName() + "': " + message);
	}

	XmlStateException createStateException(String message, Throwable cause) {
		return new XmlStateException("dd.kms.maxmlian.api.Node '" + getNodeName() + "': " + message, cause);
	}
}
