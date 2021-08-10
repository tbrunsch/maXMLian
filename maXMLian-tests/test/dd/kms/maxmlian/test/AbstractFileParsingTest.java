package dd.kms.maxmlian.test;

import dd.kms.maxmlian.api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ExtendWith({LargeXmlTestFileDeletionExtension.class})
abstract class AbstractFileParsingTest
{
	abstract void prepareTest(org.w3c.dom.Document domDocument);
	abstract int getNumberOfChildrenToParse(int depth);

	static List<Path> collectXmlFiles() throws IOException {
		Path resourceDirectory = TestUtils.getResourceDirectory();
		List<Path> xmlFiles = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(resourceDirectory)) {
			for (Path resourcePath : stream) {
				if (Files.isRegularFile(resourcePath) && isXmlFile(resourcePath)) {
					xmlFiles.add(resourcePath);
				}
			}
		}
		xmlFiles.add(TestUtils.getLargeXmlFile());
		return xmlFiles;
	}

	private static boolean isXmlFile(Path file) {
		return file.getFileName().toString().toLowerCase().endsWith(".xml");
	}

	@ParameterizedTest
	@MethodSource("collectXmlFiles")
	void testParsingFile(Path xmlFile) throws IOException, XMLStreamException, javax.xml.parsers.ParserConfigurationException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.immediateInstanceReuse().newDocumentBuilder();
		Document document = documentBuilder.parse(Files.newInputStream(xmlFile));

		javax.xml.parsers.DocumentBuilderFactory domFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		javax.xml.parsers.DocumentBuilder builder = domFactory.newDocumentBuilder();
		org.w3c.dom.Document domDocument = builder.parse(Files.newInputStream(xmlFile));

		prepareTest(domDocument);

		compareNodes(document, domDocument, 0, true);
	}

	private void compareNodes(Node node, org.w3c.dom.Node domNode, int depth, boolean recursively) throws XMLStreamException {
		String name = domNode.getNodeName();
		Assertions.assertEquals(name, node.getNodeName(), "Wrong node name");
		Assertions.assertEquals(domNode.getLocalName(), node.getLocalName(), "Wrong local name of node '" + name + "'");
		Assertions.assertEquals(domNode.getPrefix(), node.getPrefix(), "Wrong prefix of node '" + name + "'");
		Assertions.assertEquals(domNode.getNamespaceURI(), node.getNamespaceURI(), "Wrong namespace URI of node '" + name + "'");

		Node parent = node.getParentNode();
		org.w3c.dom.Node domParent = domNode.getParentNode();
		if (domParent == null) {
			if (parent != null) {
				Assertions.fail("Unexpected parent '" + parent.getNodeName() + "'");
			}
		} else {
			Assertions.assertNotNull(parent, "Expected parent '" + domParent.getNodeName() + "'");
			compareNodes(parent, domParent, depth - 1, false);
		}

		short domNodeType = domNode.getNodeType();
		NodeType expectedNodeType = getNodeType(domNodeType);
		NodeType nodeType = node.getNodeType();
		Assertions.assertEquals(expectedNodeType, nodeType, "Wrong type of node '" + name + "'");

		switch (nodeType) {
			case ELEMENT:
				compareElements((Element) node, (org.w3c.dom.Element) domNode, depth);
				break;
			case ATTRIBUTE:
				compareAttributes((Attr) node, (org.w3c.dom.Attr) domNode);
				break;
			case TEXT:
				compareTexts((Text) node, (org.w3c.dom.Text) domNode);
				break;
			case CDATA_SECTION:
				compareCDataSections((CDATASection) node, (org.w3c.dom.CDATASection) domNode);
				break;
			case ENTITY:
				compareEntities((Entity) node, (org.w3c.dom.Entity) domNode);
				break;
			case PROCESSING_INSTRUCTION:
				compareProcessingInstructions((ProcessingInstruction) node, (org.w3c.dom.ProcessingInstruction) domNode);
				break;
			case COMMENT:
				compareComments((Comment) node, (org.w3c.dom.Comment) domNode);
				break;
			case DOCUMENT:
				compareDocuments((Document) node, (org.w3c.dom.Document) domNode);
				break;
			case DOCUMENT_TYPE:
				compareDocumentTypes((DocumentType) node, (org.w3c.dom.DocumentType) domNode, depth);
				break;
			case NOTATION:
				compareNotations((Notation) node, (org.w3c.dom.Notation) domNode);
				break;
			default:
				throw new UnsupportedOperationException("The test does currently not support node type '" + nodeType + "'");
		}

		if (!recursively) {
			return;
		}

		int numChildrenToParse = getNumberOfChildrenToParse(depth);
		if (numChildrenToParse > 0) {
			Iterable<Node> children = node.getChildNodes();
			NodeList domChildren = domNode.getChildNodes();
			int numDomChildren = domChildren.getLength();
			int childIndex = 0;
			for (Node child : children) {
				Assertions.assertTrue(childIndex < numDomChildren, "Wrong number of children of node '" + name + "'");
				org.w3c.dom.Node domChild = domChildren.item(childIndex);
				compareNodes(child, domChild, depth + 1, true);
				childIndex++;
				if (childIndex >= numChildrenToParse) {
					break;
				}
			}
			if (childIndex < numChildrenToParse) {
				// this check must only be evaluated if the number of children has not been limited
				Assertions.assertEquals(numDomChildren, childIndex, "Wrong number of children of node '" + name + "'");
			}
		}
	}

	private void compareElements(Element element, org.w3c.dom.Element domElement, int depth) throws XMLStreamException {
		String name = element.getNodeName();
		Assertions.assertEquals(element.getTagName(), domElement.getTagName(), "Wrong tag name of element '" + name + "'");

		int numChildrenToParse = getNumberOfChildrenToParse(depth);
		if (numChildrenToParse > 0) {
			Map<String, Attr> attributesByName = element.getAttributes();
			org.w3c.dom.NamedNodeMap domAttributes = domElement.getAttributes();
			Assertions.assertEquals(domAttributes.getLength(), attributesByName.size(), "Wrong number of attributes of element '" + name + "'");
			int attributeIndex = 0;
			for (String attributeName : attributesByName.keySet()) {
				Attr attribute = attributesByName.get(attributeName);
				org.w3c.dom.Node domAttribute = domAttributes.getNamedItem(attributeName);
				Assertions.assertNotNull(domAttribute, "Wrong attribute name '" + attributeName + "'");
				compareNodes(attribute, domAttribute, depth + 1, true);
				attributeIndex++;
				if (attributeIndex >= numChildrenToParse) {
					break;
				}
			}
		}
	}

	private void compareAttributes(Attr attribute, org.w3c.dom.Attr domAttribute) {
		String name = domAttribute.getName();
		Assertions.assertEquals(name, attribute.getName(), "Wrong name of an attribute");
		Assertions.assertEquals(domAttribute.getValue(), attribute.getValue(), "Wrong value of attribute '" + name + "'");
		Assertions.assertEquals(domAttribute.isId(), attribute.isId(), "Wrong value of isId() of attribute '" + name + "'");
	}

	private void compareTexts(Text text, org.w3c.dom.Text domText) {
		String name = domText.getNodeName();
		Assertions.assertEquals(domText.getData(), text.getData(), "Wrong data of text node '" + name + "'");
		Assertions.assertEquals(domText.isElementContentWhitespace(), text.isElementContentWhitespace(), "Wrong value of isElementContextWhiteSpace() of text node '" + name + "'");
	}

	private void compareCDataSections(CDATASection cDataSection, org.w3c.dom.CDATASection domCDataSection) {
		String name = domCDataSection.getNodeName();
		Assertions.assertEquals(domCDataSection.getData(), cDataSection.getData(), "Wrong data of CDATA section node '" + name + "'");
		Assertions.assertEquals(domCDataSection.isElementContentWhitespace(), cDataSection.isElementContentWhitespace(), "Wrong value of isElementContextWhiteSpace() of CDATA section node '" + name + "'");
	}

	private void compareEntities(Entity entity, org.w3c.dom.Entity domEntity) {
		String name = entity.getNodeName();
		Assertions.assertEquals(domEntity.getPublicId(), entity.getPublicId(), "Wrong public ID of entity '" + name + "'");
		Assertions.assertEquals(domEntity.getSystemId(), entity.getSystemId(), "Wrong system ID of entity '" + name + "'");
		Assertions.assertEquals(domEntity.getNotationName(), entity.getNotationName(), "Wrong notation name of entity '" + name + "'");
	}

	private void compareProcessingInstructions(ProcessingInstruction processingInstruction, org.w3c.dom.ProcessingInstruction domProcessingInstruction) {
		String name = domProcessingInstruction.getNodeName();
		Assertions.assertEquals(domProcessingInstruction.getData(), processingInstruction.getData(), "Wrong data of processing instruction '" + name + "'");
		Assertions.assertEquals(domProcessingInstruction.getTarget(), processingInstruction.getTarget(), "Wrong target of processing instruction '" + name + "'");
	}

	private void compareComments(Comment comment, org.w3c.dom.Comment domComment) {
		String name = domComment.getNodeName();
		Assertions.assertEquals(domComment.getData(), comment.getData(), "Wrong data of comment '" + name + "'");
	}

	private void compareDocuments(Document document, org.w3c.dom.Document domDocument) {
		String name = domDocument.getNodeName();
		Assertions.assertEquals(domDocument.getXmlEncoding(), document.getXmlEncoding(), "Wrong XML encoding of document '" + name + "'");
		Assertions.assertEquals(domDocument.getXmlStandalone(), document.getXmlStandalone(), "Wrong value of getXmlStandalone() of document '" + name + "'");
		Assertions.assertEquals(domDocument.getXmlVersion(), document.getXmlVersion(), "Wrong XML version of document '" + name + "'");

		/*
		 * Do not test getDocumentElement() and getDoctype() because this will make document.getChildren()
		 * throw an exception because of streamed parsing. The document element and the document type will
		 * be compared nevertheless because they are part of the DOM tree.
		 */
	}

	private void compareDocumentTypes(DocumentType docType, org.w3c.dom.DocumentType domDocType, int depth) throws XMLStreamException {
		String name = domDocType.getName();
		Assertions.assertEquals(domDocType.getName(), docType.getName(), "Wrong name of document type '" + name + "'");
		Assertions.assertEquals(domDocType.getPublicId(), docType.getPublicId(), "Wrong public ID of document type '" + name + "'");
		Assertions.assertEquals(domDocType.getSystemId(), docType.getSystemId(), "Wrong system ID of document type '" + name + "'");

		int numChildrenToParse = getNumberOfChildrenToParse(depth);

		if (numChildrenToParse > 0) {
			Map<String, Entity> entitiesByName = docType.getEntities();
			org.w3c.dom.NamedNodeMap domEntities = domDocType.getEntities();
			Assertions.assertEquals(domEntities.getLength(), entitiesByName.size(), "Wrong number of entities of document type '" + name + "'");
			int entityIndex = 0;
			for (String entityName : entitiesByName.keySet()) {
				Entity entity = entitiesByName.get(entityName);
				org.w3c.dom.Node domEntity = domEntities.getNamedItem(entityName);
				Assertions.assertNotNull(domEntity, "Wrong entity name '" + entityName + "'");
				compareNodes(entity, domEntity, depth + 1, true);
				entityIndex++;
				if (entityIndex >= numChildrenToParse) {
					break;
				}
			}
		}

		if (numChildrenToParse > 0) {
			Map<String, Notation> notationsByName = docType.getNotations();
			org.w3c.dom.NamedNodeMap domNotations = domDocType.getNotations();
			Assertions.assertEquals(domNotations.getLength(), notationsByName.size(), "Wrong number of notations of document type '" + name + "'");
			int notationIndex = 0;
			for (String notationName : notationsByName.keySet()) {
				Notation notation = notationsByName.get(notationName);
				org.w3c.dom.Node domNotation = domNotations.getNamedItem(notationName);
				Assertions.assertNotNull(domNotation, "Wrong notation name '" + notationName + "'");
				compareNodes(notation, domNotation, depth + 1, true);
				notationIndex++;
				if (notationIndex >= numChildrenToParse) {
					break;
				}
			}
		}

		// Do not test getInternalSubset() because DOM does some formatting to the raw internal subset that we do not want to replicate
	}

	private void compareNotations(Notation notation, org.w3c.dom.Notation domNotation) {
		String name = domNotation.getNodeName();
		Assertions.assertEquals(domNotation.getPublicId(), notation.getPublicId(), "Wrong public ID of notation '" + name + "'");
		Assertions.assertEquals(domNotation.getSystemId(), notation.getSystemId(), "Wrong system ID of notation '" + name + "'");
	}

	private static NodeType getNodeType(short domNodeType) {
		switch (domNodeType) {
			case org.w3c.dom.Node.ATTRIBUTE_NODE:
				return NodeType.ATTRIBUTE;
			case org.w3c.dom.Node.DOCUMENT_NODE:
				return NodeType.DOCUMENT;
			case org.w3c.dom.Node.COMMENT_NODE:
				return NodeType.COMMENT;
			case org.w3c.dom.Node.TEXT_NODE:
				return NodeType.TEXT;
			case org.w3c.dom.Node.ELEMENT_NODE:
				return NodeType.ELEMENT;
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				return NodeType.CDATA_SECTION;
			case org.w3c.dom.Node.DOCUMENT_TYPE_NODE:
				return NodeType.DOCUMENT_TYPE;
			case org.w3c.dom.Node.ENTITY_NODE:
				return NodeType.ENTITY;
			case org.w3c.dom.Node.NOTATION_NODE:
				return NodeType.NOTATION;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				return NodeType.PROCESSING_INSTRUCTION;
			default:
				throw new UnsupportedOperationException("DOM node type " + domNodeType + " is currently not supported");
		}
	}
}
