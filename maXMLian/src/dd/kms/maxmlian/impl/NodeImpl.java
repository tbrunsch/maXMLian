package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Attr;
import dd.kms.maxmlian.api.Node;
import dd.kms.maxmlian.api.XmlException;
import dd.kms.maxmlian.api.XmlStateException;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

// The class is also an Iterable over its own children
abstract class NodeImpl implements Node, Iterable<Node>
{
	private static final String	PARSE_SIBLING_ERROR		= "Cannot parse sibling when the XML reader has already parsed beyond the end of that node";

	private final ExtendedXmlStreamReader	streamReader;
	private final NodeFactory				nodeFactory;

	// Positional information
	private long							initialPosition;
	private int								initialDepth;
	private long                          	initialNodeCounterDepth;

	private Node							parent;
	private final StringBuilder				stringBuilder				= new StringBuilder();

	NodeImpl(ExtendedXmlStreamReader streamReader, NodeFactory nodeFactory) {
		this.streamReader = streamReader;
		this.nodeFactory = nodeFactory;
	}

	void initialize() {
		initialPosition = streamReader.position();
		initialDepth = streamReader.getDepth();
		initialNodeCounterDepth = streamReader.getNodeCounter(initialDepth);
		parent = null;
	}

	void setParentNode(Node parent) {
		this.parent = parent;
	}

	XMLStreamReader getReader() {
		return streamReader.getReader();
	}

	void resetReaderPosition(String errorMessage) {
		try {
			if (!streamReader.position(initialPosition)) {
				throw createStateException(errorMessage);
			}
		} catch (XMLStreamException e) {
			throw createStateException(errorMessage, e);
		}
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
		return Collections.emptyList();
	}

	@Override
	public Iterator<Node> iterator() {
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
		if (streamReader.getDepth() < initialDepth || streamReader.getNodeCounter(initialDepth) != initialNodeCounterDepth) {
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

	@Override
	public String getTextContent() throws XMLStreamException {
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

	Map<String, Attr> createAttributesByQNameMap() {
		return nodeFactory.createAttributesByQNameMap(initialDepth);
	}

	NamespaceImpl createNamespace(String localName, String value) {
		return nodeFactory.createNamespace(localName, value, initialDepth);
	}

	AttrImpl createAttribute(String namespaceUri, String localName, String prefix, String value, String type) {
		return nodeFactory.createAttribute(namespaceUri, localName, prefix, value, type, initialDepth);
	}

	void appendTextContentTo(StringBuilder builder) throws XMLStreamException {
		for (Node child : getChildNodes()) {
			((NodeImpl) child).appendTextContentTo(builder);
		}
	}

	private XmlStateException createStateException(String message) {
		return new XmlStateException(getClass() + " '" + getNodeName() + "': " + message);
	}

	private XmlStateException createStateException(String message, Throwable cause) {
		return new XmlStateException(getClass() + " '" + getNodeName() + "': " + message, cause);
	}
}
