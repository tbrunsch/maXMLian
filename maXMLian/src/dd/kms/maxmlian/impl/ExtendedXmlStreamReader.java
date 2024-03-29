package dd.kms.maxmlian.impl;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.util.Arrays;

class ExtendedXmlStreamReader
{
	private final XMLStreamReader	reader;

	private long					position;
	private long[]					nodeCounterByDepth	= new long[10];
	private int   					depth				= -1;
	private int						nextDepth			= 0;
	private boolean					peekedEvent			= true;			// the start document event is initially available

	ExtendedXmlStreamReader(XMLStreamReader reader) {
		this.reader = reader;
	}

	XMLStreamReader getReader() {
		return reader;
	}

	boolean hasNext() throws XMLStreamException {
		return reader.hasNext();
	}

	void next() throws XMLStreamException {
		if (peekedEvent) {
			peekedEvent = false;
		} else {
			reader.next();
		}

		depth = nextDepth;
		position++;
		switch (reader.getEventType()) {
			case XMLEvent.START_DOCUMENT:
			case XMLEvent.START_ELEMENT:
				nextDepth = depth + 1;
				incrementNodeCounter(depth);
				break;
			case XMLEvent.END_DOCUMENT:
			case XMLEvent.END_ELEMENT:
				depth--;
				nextDepth = depth;
				break;
			default:
				nextDepth = depth;
				incrementNodeCounter(depth);
				break;
		}
	}

	void peek() throws XMLStreamException {
		if (!peekedEvent) {
			reader.next();
			peekedEvent = true;
		}
	}

	long position() {
		return position;
	}

	boolean position(long position) {
		/*
		 * Setting the position is possible in general, but not as long as maXMLian is
		 * based on StAX. The reason is that the StAX parser has an internal state:
		 *
		 * com.sun.org.apache.xerces.internal.impl.XMLStreamReaderImpl has a field
		 * fScanner of type com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl,
		 * which extends com.sun.org.apache.xerces.internal.impl.XMLDocumentFragmentScannerImpl,
		 * which manages, among others, a stack of opened entities (field fEntityStack).
		 *
		 * Since maXMLian has no chance to update this state, changing the position would
		 * leave the StAX parser in a state that does not reflect the new position.
		 */
		return this.position == position;
	}

	long getNodeCounter(int depth) {
		return nodeCounterByDepth[depth];
	}

	/**
	 * @return the depth of the node belonging to the last parsed XML event
	 */
	int getDepth() {
		return depth;
	}

	/**
	 * @return the depth a node would have when opened here
	 */
	int getNextDepth() {
		return nextDepth;
	}

	private void incrementNodeCounter(int depth) {
		if (depth >= nodeCounterByDepth.length) {
			nodeCounterByDepth = Arrays.copyOf(nodeCounterByDepth, 2*nodeCounterByDepth.length);
		}
		nodeCounterByDepth[depth]++;
	}
}
