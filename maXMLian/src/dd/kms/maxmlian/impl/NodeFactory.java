package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;
import java.util.List;
import java.util.Map;

import static javax.xml.stream.XMLStreamConstants.*;

class NodeFactory
{
	private static final String		PROPERTY_ENTITIES		= "javax.xml.stream.entities";
	private static final String		PROPERTY_NOTATIONS		= "javax.xml.stream.notations";

	private final ExtendedXmlStreamReader	streamReader;
	private final XMLStreamReader			reader;
	private final ObjectFactory				objectFactory;
	private final StringBuilder				additionalCharactersBuilder	= new StringBuilder();

	NodeFactory(ExtendedXmlStreamReader streamReader, int reuseDelay) {
		this.streamReader = streamReader;
		this.reader = streamReader.getReader();
		this.objectFactory =	reuseDelay == ImplUtils.INSTANCE_REUSE_IMMEDIATE	? new ObjectFactoryImmediateReuse(streamReader, this) :
								reuseDelay == ImplUtils.INSTANCE_REUSE_NONE			? new DefaultObjectFactory(streamReader, this)
																					: new ObjectFactoryDelayedReuse(streamReader, this, reuseDelay);
	}

	ChildIterator createChildIterator() {
		ChildIterator iterator = objectFactory.createChildIterator(streamReader.getDepth());
		iterator.initialize();
		return iterator;
	}

	Node readFirstChildNode() throws XMLStreamException {
		int depth = streamReader.getDepth();
		if (streamReader.getNextDepth() == depth) {
			return null;
		}
		if (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.getDepth() <= depth) {
				// parent node closed => no children
				return null;
			}
			return createNode();
		}
		return null;
	}

	Node getNextSibling(int depth) throws XMLStreamException {
		while (streamReader.hasNext()) {
			streamReader.next();
			int currentDepth = streamReader.getDepth();
			if (currentDepth < depth) {
				// no next sibling available
				return null;
			} else if (currentDepth == depth) {
				Node nextSibling = createNode();
				if (nextSibling != null) {
					return nextSibling;
				}
			}
		}
		return null;
	}

	private Node createNode() throws XMLStreamException {
		int eventType = reader.getEventType();
		switch (eventType) {
			case START_ELEMENT:
				return createElement();

			case END_ELEMENT:
				return null;

			case CHARACTERS:
			case SPACE:
				return createText(eventType);

			case CDATA:
				return createCData();

			case COMMENT:
				return createComment();

			case PROCESSING_INSTRUCTION:
				return createProcessingInstruction();

			case START_DOCUMENT:
				return createDocument();

			case END_DOCUMENT:
				return null;

			case DTD:
				return createDocumentType();

			default:
			{
				throw new XMLStreamException("Unsupported XML event type: " + eventType);
			}
		}
	}

	private DocumentType createDocumentType() {
		DocumentTypeImpl documentType = new DocumentTypeImpl(streamReader, this);
		String documentTypeDeclaration = reader.getText();
		List<EntityDeclaration> entityDeclarations = (List<EntityDeclaration>) reader.getProperty(PROPERTY_ENTITIES);
		List<NotationDeclaration> notationDeclarations = (List<NotationDeclaration>) reader.getProperty(PROPERTY_NOTATIONS);
		documentType.initialize(documentTypeDeclaration, entityDeclarations, notationDeclarations);
		return documentType;
	}

	private Element createElement() {
		ElementImpl element = objectFactory.createElement(streamReader.getDepth());
		element.initialize(reader.getNamespaceURI(), reader.getLocalName(), reader.getPrefix());
		return element;
	}

	private Text createText(int eventType) throws XMLStreamException {
		int depth = streamReader.getDepth();
		TextImpl text = objectFactory.createText(depth);
		text.initialize(reader.getText(), readAdditionalCharacters(eventType), eventType == SPACE);
		return text;
	}

	private Text createCData() throws XMLStreamException {
		int depth = streamReader.getDepth();
		TextImpl text = objectFactory.createCDataSection(depth);
		text.initialize(reader.getText(), readAdditionalCharacters(CDATA), false);
		return text;
	}

	private Comment createComment() {
		CommentImpl comment = objectFactory.createComment(streamReader.getDepth());
		comment.initialize(reader.getText());
		return comment;
	}

	/**
	 * @return the concatenated text of all successive events of type {@code charactersEventType}.
	 */
	private StringBuilder readAdditionalCharacters(int charactersEventType) throws XMLStreamException {
		additionalCharactersBuilder.setLength(0);
		while (streamReader.hasNext()) {
			streamReader.peek();
			if (reader.getEventType() != charactersEventType) {
				break;
			}
			additionalCharactersBuilder.append(reader.getText());
			streamReader.next();
		}
		return additionalCharactersBuilder;
	}

	private ProcessingInstruction createProcessingInstruction() {
		ProcessingInstructionImpl procInstruction = objectFactory.createProcessingInstruction(streamReader.getDepth());
		procInstruction.initialize(reader.getPITarget(), reader.getPIData());
		return procInstruction;
	}

	private Document createDocument() {
		DocumentImpl document = new DocumentImpl(streamReader, this);
		document.initialize(reader.getCharacterEncodingScheme(), reader.isStandalone(), reader.getVersion());
		return document;
	}

	Map<String, Attr> createAttributesByQNameMap(int depth) {
		return objectFactory.createAttributesByQNameMap(depth);
	}

	NamespaceImpl createNamespace(String localName, String value, int depth) {
		NamespaceImpl namespace = objectFactory.createNamespace(depth);
		namespace.initialize("http://www.w3.org/2000/xmlns/", localName, "xmlns", value, null);
		return namespace;
	}

	AttrImpl createAttribute(String namespaceUri, String localName, String prefix, String value, String type, int depth) {
		AttrImpl attr = objectFactory.createAttribute(depth);
		attr.initialize(namespaceUri, localName, prefix, value, type);
		return attr;
	}
}
