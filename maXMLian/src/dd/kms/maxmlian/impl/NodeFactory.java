package dd.kms.maxmlian.impl;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Pattern;

import static javax.xml.stream.XMLStreamConstants.*;

class NodeFactory
{
	private static final Pattern 	DOCTYPE_PATTERN						= Pattern.compile("^\\s*<!DOCTYPE\\s+.*", Pattern.DOTALL);

	private static final String		PROPERTY_ENTITIES					= "javax.xml.stream.entities";
	private static final String		PROPERTY_NOTATIONS					= "javax.xml.stream.notations";
	private static final String		PARSE_NEXT_TEXT_CONTENT_PART_ERROR	= "Cannot parse next part of text content when the XML reader has already parsed beyond the position of that part";

	private final ExtendedXmlStreamReader	streamReader;
	private final XMLStreamReader			reader;
	private final ObjectFactory				objectFactory;
	private final boolean					namespaceAware;
	private final StringBuilder				stringBuilder	= new StringBuilder();

	NodeFactory(ExtendedXmlStreamReader streamReader, boolean reuseInstances, boolean namespaceAware) {
		this.streamReader = streamReader;
		this.reader = streamReader.getReader();
		this.namespaceAware = namespaceAware;
		this.objectFactory = reuseInstances
				? new ObjectFactoryWithReuse(streamReader, this)
				: new DefaultObjectFactory(streamReader, this);
	}

	boolean isNamespaceAware() {
		return namespaceAware;
	}

	NodeImpl readFirstChild() throws XMLStreamException {
		return readUntilFirstChild() ? createNode() : null;
	}

	NodeImpl getNextSibling(int depth) throws XMLStreamException {
		return readUntilNextSibling(depth) ? createNode() : null;
	}

	ElementImpl readFirstChildElement() throws XMLStreamException {
		if (!readUntilFirstChild()) {
			return null;
		}
		if (reader.getEventType() == START_ELEMENT) {
			return createElement();
		}
		int childDepth = streamReader.getDepth();
		return getNextSiblingElement(childDepth);
	}

	ElementImpl getNextSiblingElement(int depth) throws XMLStreamException {
		while (readUntilNextSibling(depth)) {
			if (reader.getEventType() == START_ELEMENT) {
				return createElement();
			}
		}
		return null;
	}

	String getTextContent(NodeImpl node) throws XMLStreamException {
		long expectedPosition = node.getInitialPosition();
		stringBuilder.setLength(0);
		String textContentPart;
		while ((textContentPart = getNextTextContentPart(node, expectedPosition)) != null) {
			stringBuilder.append(textContentPart);
			expectedPosition = streamReader.position();
		}
		return stringBuilder.toString();
	}

	String getNextTextContentPart(NodeImpl node, long expectedPosition) throws XMLStreamException {
		if (!streamReader.position(expectedPosition)) {
			throw node.createStateException(PARSE_NEXT_TEXT_CONTENT_PART_ERROR);
		}
		int nodeDepth = node.getInitialDepth();
		while (streamReader.hasNext()) {
			streamReader.next();
			int currentDepth = streamReader.getDepth();
			if (currentDepth <= nodeDepth) {
				// reached end of node
				return null;
			}
			int eventType = reader.getEventType();
			switch (eventType) {
				case CHARACTERS:
				case CDATA:
					return reader.getText();
				default:
					// other events can be ignored for determining the text content
					break;
			}
		}
		return null;
	}

	private boolean readUntilFirstChild() throws XMLStreamException {
		int depth = streamReader.getDepth();
		if (streamReader.getNextDepth() == depth) {
			return false;
		}
		if (streamReader.hasNext()) {
			streamReader.next();
			if (streamReader.getDepth() <= depth) {
				// parent node closed => no children
				return false;
			}
			return true;
		}
		return false;
	}

	private boolean readUntilNextSibling(int depth) throws XMLStreamException {
		while (streamReader.hasNext()) {
			streamReader.next();
			int currentDepth = streamReader.getDepth();
			if (currentDepth < depth) {
				// no next sibling available
				return false;
			} else if (currentDepth == depth) {
				int eventType = reader.getEventType();
				if (eventType != END_ELEMENT && eventType != END_DOCUMENT) {
					return true;
				}
			}
		}
		return false;
	}

	private NodeImpl createNode() throws XMLStreamException {
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
				throw new XMLStreamException("Unsupported XML event type: " + eventType);
		}
	}

	private AbstractDocumentTypeImpl createDocumentType() throws XMLStreamException {
		String text = reader.getText();
		List<EntityDeclaration> entityDeclarations = (List<EntityDeclaration>) reader.getProperty(PROPERTY_ENTITIES);
		List<NotationDeclaration> notationDeclarations = (List<NotationDeclaration>) reader.getProperty(PROPERTY_NOTATIONS);

		/*
		 * According to the StAX specification, the text should only contain the internal subset.
		 * However, the common StAX parsers Xerces returns the full document type declaration.
		 * Hence, we have to treat this parser differently.
		 */
		boolean containsFullDocTypeDeclaration = DOCTYPE_PATTERN.matcher(text).matches();
		if (containsFullDocTypeDeclaration) {
			DocumentTypeFromFullDtdText documentType = new DocumentTypeFromFullDtdText(streamReader, this);
			String documentTypeDeclaration = text;
			documentType.initialize(documentTypeDeclaration, entityDeclarations, notationDeclarations);
			return documentType;
		}

		String internalSubset = text;

		/*
		 * The StAX API does not provide a way to obtain the document's root name, the public id,
		 * and the system id. The FasterXML StAX2 API does, but we do not want to hard-code against
		 * its classes. Hence, we try to call the methods via reflection.
		 */
		String documentTypeName = getStringFromReader("getDTDRootName", "Cannot obtain name of document type declaration");
		String publicId = getStringFromReader("getDTDPublicId", "Cannot obtain public id of document type declaration");
		String systemId = getStringFromReader("getDTDSystemId", "Cannot obtain public id of document type declaration");
		DocumentTypeImpl documentType = new DocumentTypeImpl(streamReader, this);
		documentType.initialize(documentTypeName, publicId, systemId, internalSubset, entityDeclarations, notationDeclarations);
		return documentType;
	}

	private ElementImpl createElement() {
		ElementImpl element = objectFactory.createElement(streamReader.getDepth());
		element.initialize(reader.getNamespaceURI(), reader.getLocalName(), reader.getPrefix());
		return element;
	}

	private TextImpl createText(int eventType) {
		int depth = streamReader.getDepth();
		TextImpl text = objectFactory.createText(depth);
		text.initialize(reader.getText(), eventType == SPACE);
		return text;
	}

	private TextImpl createCData() {
		int depth = streamReader.getDepth();
		TextImpl text = objectFactory.createCDataSection(depth);
		text.initialize(reader.getText(), false);
		return text;
	}

	private CommentImpl createComment() {
		CommentImpl comment = objectFactory.createComment(streamReader.getDepth());
		comment.initialize(reader.getText());
		return comment;
	}

	private ProcessingInstructionImpl createProcessingInstruction() {
		ProcessingInstructionImpl procInstruction = objectFactory.createProcessingInstruction(streamReader.getDepth());
		procInstruction.initialize(reader.getPITarget(), reader.getPIData());
		return procInstruction;
	}

	private DocumentImpl createDocument() {
		DocumentImpl document = new DocumentImpl(streamReader, this);
		document.initialize(reader.getCharacterEncodingScheme(), reader.isStandalone(), reader.getVersion());
		return document;
	}

	NamedAttributeMapImpl createNamedAttributeMap(int depth) {
		return objectFactory.createNamedAttributeMap(depth);
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

	private String getStringFromReader(String methodName, String errorMessage) throws XMLStreamException {
		try {
			Method method = reader.getClass().getMethod(methodName);
			return (String) method.invoke(reader);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			throw new XMLStreamException(errorMessage);
		}
	}
}
