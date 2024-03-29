package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

abstract class NodeImpl implements Node
{
	private static final String	PARSE_FIRST_CHILD_ERROR	= "Cannot parse first child when the XML reader has already parsed beyond the start position of that node";
	private static final String	PARSE_SIBLING_ERROR		= "Cannot parse sibling when the XML reader has already parsed beyond the end of that node";

	private final ExtendedXmlStreamReader	streamReader;
	private final NodeFactory				nodeFactory;

	// Positional information
	private long							initialPosition;
	private int								initialDepth;
	private long                          	initialNodeCounterDepth;

	private Node							parent;

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

	void resetReaderPosition(String errorMessage) throws XmlStateException {
		if (!streamReader.position(initialPosition)) {
			throw createStateException(errorMessage);
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
	public NodeImpl getFirstChild() throws XmlException, XmlStateException {
		resetReaderPosition(PARSE_FIRST_CHILD_ERROR);
		try {
			NodeImpl firstChild = nodeFactory.readFirstChild();
			if (firstChild != null) {
				firstChild.setParentNode(this);
			}
			return firstChild;
		} catch (XMLStreamException e) {
			throw new XmlException("Cannot read first child of node '" + getNodeName() + "': " + e, e);
		}
	}

	@Override
	public NodeImpl getNextSibling() throws XmlException, XmlStateException {
		if (streamReader.getDepth() < initialDepth || streamReader.getNodeCounter(initialDepth) != initialNodeCounterDepth) {
			resetReaderPosition(PARSE_SIBLING_ERROR);
		}
		try {
			// store parent reference because it will be cleared if this node is reused
			Node parent = this.parent;
			NodeImpl nextSibling = nodeFactory.getNextSibling(initialDepth);
			if (nextSibling != null) {
				nextSibling.setParentNode(parent);
			}
			return nextSibling;
		} catch (XMLStreamException e) {
			throw new XmlException("Cannot read next sibling of node '" + getNodeName() + "': " + e, e);
		}
	}

	@Override
	public Element getFirstChildElement() throws XmlException, XmlStateException {
		resetReaderPosition(PARSE_FIRST_CHILD_ERROR);
		try {
			ElementImpl firstChildElement = nodeFactory.readFirstChildElement();
			if (firstChildElement != null) {
				firstChildElement.setParentNode(this);
			}
			return firstChildElement;
		} catch (XMLStreamException e) {
			throw new XmlException("Cannot read first child element of node '" + getNodeName() + "': " + e, e);
		}
	}

	@Override
	public Element getNextSiblingElement() throws XmlException, XmlStateException {
		if (streamReader.getDepth() < initialDepth || streamReader.getNodeCounter(initialDepth) != initialNodeCounterDepth) {
			resetReaderPosition(PARSE_SIBLING_ERROR);
		}
		try {
			// store parent reference because it will be cleared if this node is reused
			Node parent = this.parent;
			ElementImpl nextSiblingElement = nodeFactory.getNextSiblingElement(initialDepth);
			if (nextSiblingElement != null) {
				nextSiblingElement.setParentNode(parent);
			}
			return nextSiblingElement;
		} catch (XMLStreamException e) {
			throw new XmlException("Cannot read next sibling element of node '" + getNodeName() + "': " + e, e);
		}
	}

	@Override
	public NamedAttributeMap getAttributes() {
		return EmptyNamedAttributeMap.ATTRIBUTE_MAP;
	}

	@Override
	public String getTextContent() throws XmlException, XmlStateException {
		try {
			return nodeFactory.getTextContent(this);
		} catch (XMLStreamException e) {
			throw new XmlException("Cannot read text content of node '" + getNodeName() + "': " + e, e);
		}
	}

	@Override
	public StringStream getTextContentStream() {
		return new NodeTextContentStream();
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

	long getInitialPosition() {
		return initialPosition;
	}

	int getInitialDepth() {
		return initialDepth;
	}

	boolean isNamespaceAware() {
		return nodeFactory.isNamespaceAware();
	}

	NamedAttributeMapImpl createNamedAttributeMap() {
		return nodeFactory.createNamedAttributeMap(initialDepth);
	}

	NamespaceImpl createNamespace(String localName, String value) {
		return nodeFactory.createNamespace(localName, value, initialDepth);
	}

	AttrImpl createAttribute(String namespaceUri, String localName, String prefix, String value, String type) {
		return nodeFactory.createAttribute(namespaceUri, localName, prefix, value, type, initialDepth);
	}

	XmlStateException createStateException(String message) {
		return new XmlStateException(getClass() + " '" + getNodeName() + "': " + message);
	}

	private class NodeTextContentStream implements StringStream
	{
		private long expectedPosition;

		NodeTextContentStream() {
			this.expectedPosition = getInitialPosition();
		}

		@Override
		public String next() throws XmlException, XmlStateException {
			try {
				String nextTextContentPart = nodeFactory.getNextTextContentPart(NodeImpl.this, expectedPosition);
				expectedPosition = streamReader.position();
				return nextTextContentPart;
			} catch (XMLStreamException e) {
				throw new XmlException("Cannot read next text content part of node '" + getNodeName() + "': " + e, e);
			}
		}
	}
}
