import java.util.Stack;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

class ExtendedXmlEventReader
{
	private final XMLEventReader	reader;

	private long					position;
	private long[]					nodeCounterByDepth	= new long[10];
	private int   					depth				= -1;
	private int						nextDepth			= 0;

	ExtendedXmlEventReader(XMLEventReader reader) {
		this.reader = reader;
	}

	boolean hasNext() {
		return reader.hasNext();
	}

	XMLEvent nextEvent() throws XMLStreamException {
		XMLEvent event = reader.nextEvent();

		depth = nextDepth;
		position++;
		switch (event.getEventType()) {
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
		return event;
	}

	XMLEvent peek() throws XMLStreamException {
		return reader.peek();
	}

	long position() {
		return position;
	}

	boolean position(long position) throws XMLStreamException {
		while (this.position < position && hasNext()) {
			nextEvent();
		}
		if (this.position < position) {
			throw new IllegalStateException("The requested position " + position + " is beyond the maximum position " + this.position);
		}
		if (this.position == position) {
			return true;
		}
		// Currently we do not support moving back in the XML file
		return false;
	}

	long getNodeCounter(int depth) {
		return nodeCounterByDepth[depth];
	}

	/**
	 * @return the depth of the node belonging to the last parsed XMLEvent
	 */
	int getDepth() {
//		Problem: Knoten, die geöffnet und gleich wieder geschlossen werden, können über depth nicht korrekt ausgedrückt werden
		return depth;
	}

	// TODO: Add JavaDoc
	int getNextDepth() {
		return nextDepth;
	}

	private void incrementNodeCounter(int depth) {
		int depthLimit = nodeCounterByDepth.length;
		if (depth >= depthLimit) {
			long[] newNodeCounterByDepth = new long[2*depthLimit];
			for (int i = 0; i < depthLimit; i++) {
				newNodeCounterByDepth[i] = nodeCounterByDepth[i];
			}
			nodeCounterByDepth = newNodeCounterByDepth;
		}
		nodeCounterByDepth[depth]++;
	}
}
