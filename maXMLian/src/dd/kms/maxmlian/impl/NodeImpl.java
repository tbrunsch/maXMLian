package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.NamedAttributeMap;
import dd.kms.maxmlian.api.Node;
import dd.kms.maxmlian.api.XmlException;
import dd.kms.maxmlian.api.XmlStateException;

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
	public NodeImpl getFirstChild() throws XmlException {
		resetReaderPosition(PARSE_FIRST_CHILD_ERROR);
		try {
			NodeImpl firstChild = nodeFactory.readFirstChildNode();
			if (firstChild != null) {
				firstChild.setParentNode(this);
			}
			return firstChild;
		} catch (XMLStreamException e) {
			throw new XmlException("Cannot read first child of node '" + getNodeName() + "': " + e, e);
		}
	}

	@Override
	public NodeImpl getNextSibling() throws XmlException {
		if (streamReader.getDepth() < initialDepth || streamReader.getNodeCounter(initialDepth) != initialNodeCounterDepth) {
			resetReaderPosition(PARSE_SIBLING_ERROR);
		}
		try {
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
	public NamedAttributeMap getAttributes() {
		return EmptyNamedAttributeMap.ATTRIBUTE_MAP;
	}

	@Override
	public String getTextContent() throws XmlException {
		stringBuilder.setLength(0);
		try {
			appendTextContentTo(stringBuilder);
		} catch (XmlException e) {
			throw new XmlException("Cannot read text content of node '" + getNodeName() + "': " + e, e);
		}
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

	void appendTextContentTo(StringBuilder builder) throws XmlException {
		for (Node child = getFirstChild(); child != null; child = child.getNextSibling()) {
			((NodeImpl) child).appendTextContentTo(builder);
		}
	}

	private XmlStateException createStateException(String message) {
		return new XmlStateException(getClass() + " '" + getNodeName() + "': " + message);
	}
}
