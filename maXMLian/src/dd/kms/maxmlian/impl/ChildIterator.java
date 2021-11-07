package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Node;

import java.util.Iterator;

class ChildIterator implements Iterator<Node>
{
	private final ExtendedXmlStreamReader	streamReader;

	private boolean 	retrievedNext;
	private NodeImpl   	next;
	private NodeImpl	parent;

	ChildIterator(ExtendedXmlStreamReader streamReader) {
		this.streamReader = streamReader;
	}

	void initialize() {
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
		next = next == null ? parent.getFirstChild() : next.getNextSibling();
		retrievedNext = true;
	}
}
