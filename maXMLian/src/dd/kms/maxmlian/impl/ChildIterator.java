package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Node;
import dd.kms.maxmlian.api.XmlException;

import javax.xml.stream.XMLStreamException;
import java.util.Iterator;

class ChildIterator implements Iterator<Node>
{
	private static final String	PARSE_CHILDREN_ERROR	= "Cannot parse children when the XML reader has already parsed beyond the start position of that node";

	private final ExtendedXmlStreamReader	streamReader;
	private final NodeFactory				nodeFactory;

	private long		initialPosition;
	private boolean 	retrievedNext;
	private NodeImpl   	next;
	private NodeImpl	parent;

	ChildIterator(ExtendedXmlStreamReader streamReader, NodeFactory nodeFactory) {
		this.streamReader = streamReader;
		this.nodeFactory = nodeFactory;
	}

	void initialize() {
		initialPosition = streamReader.position();
		retrievedNext = false;
		next = null;
	}

	void setParentNode(NodeImpl parent) {
		this.parent = parent;
	}

	@Override
	public boolean hasNext() {
		if (!retrievedNext) {
			retrieveNext();
		}
		return next != null;
	}

	@Override
	public Node next() {
		if (!retrievedNext) {
			retrieveNext();
		}
		retrievedNext = false;
		return next;
	}

	private void retrieveNext() {
		assert !retrievedNext : "retrieveNext() must only be called if the next element has not yet been retrieved";
		if (next != null) {
			next = next.getNextSibling();
		} else {
			parent.resetReaderPosition(PARSE_CHILDREN_ERROR);
			try {
				next = nodeFactory.readFirstChildNode();
			} catch (XMLStreamException e) {
				throw new XmlException("Cannot read next child from XML: " + e.getMessage(), e);
			}
		}
		if (next != null) {
			next.setParentNode(parent);
		}
		retrievedNext = true;
	}
}
