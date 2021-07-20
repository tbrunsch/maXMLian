package dd.kms.maxmlian.impl;

import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import dd.kms.maxmlian.api.XmlException;
import dd.kms.maxmlian.api.XmlStateException;
import dd.kms.maxmlian.api.Node;

class ChildIterator implements Iterator<Node>
{
	private final ExtendedXmlEventReader	eventReader;
	private final NodeFactory				nodeFactory;

	private int     depth;
	private boolean retrievedNext;
	private Node    next;
	private Node	parent;

	ChildIterator(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		this.eventReader = eventReader;
		this.nodeFactory = nodeFactory;
	}

	void initialize() {
		depth = eventReader.getDepth();
		retrievedNext = false;
		next = null;
	}

	void setParentNode(Node parent) {
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
			if (eventReader.getDepth() != depth) {
				throw new XmlStateException("Cannot access first child because the XML reader has already parsed beyond the start position of the node");
			}
			try {
				next = nodeFactory.readFirstChildNode();
			} catch (XMLStreamException e) {
				throw new XmlException("Cannot read next child from XML: " + e.getMessage(), e);
			}
		}
		if (next != null) {
			((NodeImpl) next).setParentNode(parent);
		}
		retrievedNext = true;
	}
}
