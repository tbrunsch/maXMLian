package dd.kms.maxmlian.impl;

import static javax.xml.stream.XMLStreamConstants.*;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.*;

import dd.kms.maxmlian.api.*;

class NodeFactory
{
	private final ExtendedXmlEventReader	eventReader;

	NodeFactory(ExtendedXmlEventReader eventReader) {
		this.eventReader = eventReader;
	}

	ChildIterator createChildIterator() {
		int depth = eventReader.getDepth();
		// TODO: Reuse NodesImpl instances for childDepth
		ChildIterator iterator = new ChildIterator(eventReader, this);
		iterator.initialize();
		return iterator;
	}

	Node readFirstChildNode() throws XMLStreamException {
		int depth = eventReader.getDepth();
		if (eventReader.getNextDepth() == depth) {
			return null;
		}
		// use a while loop here to skip unsupported events
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			if (eventReader.getDepth() <= depth) {
				// parent node closed => no children, hand back this event
				return null;
			}
			return createNode(event);
		}
		return null;
	}

	Node getNextSibling(int depth) throws XMLStreamException {
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			int currentDepth = eventReader.getDepth();
			if (currentDepth < depth) {
				// no next sibling available
				return null;
			} else if (currentDepth == depth) {
				Node nextSibling = createNode(event);
				if (nextSibling != null) {
					return nextSibling;
				}
			}
		}
		return null;
	}

	private Node createNode(XMLEvent event) throws XMLStreamException {
		int eventType = event.getEventType();
		switch (eventType) {
			case START_ELEMENT:
				return createElement(event.asStartElement());

			case END_ELEMENT:
				return null;

			case CHARACTERS: {
				Characters characters = event.asCharacters();
				/*
				 * The StAX parser does not read all characters at once if there are too many,
				 * but in DOM only one text node is created => read further characters if
				 * available.
				 */
				List<Characters> additionalCharacters = readAdditionalCharacters(characters.getEventType(), characters.isCData());
				return createText(characters, additionalCharacters);
			}

			case COMMENT:
				return createComment((javax.xml.stream.events.Comment) event);

			case NAMESPACE:
				// no idea what to do with it
				return null;

			case PROCESSING_INSTRUCTION:
				return createProcessingInstruction((javax.xml.stream.events.ProcessingInstruction) event);

			case START_DOCUMENT:
				return createDocument((StartDocument) event);

			case END_DOCUMENT:
				return null;

			case DTD:
				return createDocumentType((DTD) event);

			default:
			{
				throw new XMLStreamException("Unsupported XML event type: " + eventType);
			}
		}
	}

	private DocumentType createDocumentType(DTD dtd) {
		DocumentTypeImpl documentType = new DocumentTypeImpl(eventReader, this);
		documentType.initializeFromDtd(dtd);
		return documentType;

	}

	private Element createElement(StartElement startElement) {
		// TODO: Later we might reuse instances of the current depth in order to reduce the number of instantiations
		ElementImpl element = new ElementImpl(eventReader, this);
		element.initializeFromStartElement(startElement);
		return element;
	}

	private Text createText(Characters characters, List<Characters> additionalCharacters) {
		// TODO: Later we might reuse instances of the current depth in order to reduce the number of instantiations
		TextImpl text = characters.isCData() ? new CDATASectionImpl(eventReader, this) : new TextImpl(eventReader, this);
		text.initializeFromCharacters(characters, additionalCharacters);
		return text;
	}

	Text createText(String data, int depth) {
		// TODO: Later we might reuse instances of the specified depth
		TextImpl text = new TextImpl(eventReader, this);
		text.initializeFromData(data);
		return text;
	}

	private dd.kms.maxmlian.api.Comment createComment(javax.xml.stream.events.Comment event) {
		// TODO: Later we might reuse instances of the current depth in order to reduce the number of instantiations
		CommentImpl comment = new CommentImpl(eventReader, this);
		comment.initializeFromComment(event);
		return comment;
	}

	/**
	 * Reads as many successive CHARACTERS events of type {@code charactersEventType} and that are CDATA if and only if
	 * {@code isCData} is set as available. If no such events are available, then null is returned.
	 */
	private List<Characters> readAdditionalCharacters(int charactersEventType, boolean isCData) throws XMLStreamException {
		List<Characters> additionalCharacters = null;
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.peek();
			if (event.getEventType() != charactersEventType) {
				break;
			}
			Characters characters = event.asCharacters();
			if (characters.isCData() != isCData){
				break;
			}
			if (additionalCharacters == null) {
				// TODO Later we might reuse instances of the current depth in order to reduce the number of instantiations
				additionalCharacters = new ArrayList<>();
			}
			additionalCharacters.add(characters);
			eventReader.nextEvent();
		}
		return additionalCharacters;
	}

	private dd.kms.maxmlian.api.ProcessingInstruction createProcessingInstruction(javax.xml.stream.events.ProcessingInstruction processingInstruction) {
		// TODO: Later we might reuse instances of the current depth in order to reduce the number of instantiations
		ProcessingInstructionImpl procInstruction = new ProcessingInstructionImpl(eventReader, this);
		procInstruction.initializeFromProcessingInstruction(processingInstruction);
		return procInstruction;
	}

	private Document createDocument(StartDocument startDocument) {
		DocumentImpl document = new DocumentImpl(eventReader, this);
		document.initializeFromStartDocument(startDocument);
		return document;
	}

	NamespaceImpl createNamespace(javax.xml.stream.events.Namespace ns, int depth) {
		// TODO: Later we might reuse instances of the current depth in order to reduce the number of instantiations
		NamespaceImpl namespace = new NamespaceImpl(this);
		namespace.initializeFromNamespace(ns, depth);
		return namespace;
	}

	AttrImpl createAttribute(javax.xml.stream.events.Attribute attribute, int depth) {
		// TODO: Later we might reuse instances of the current depth in order to reduce the number of instantiations
		AttrImpl attr = new AttrImpl(this);
		attr.initializeFromAttribute(attribute, depth);
		return attr;
	}
}
