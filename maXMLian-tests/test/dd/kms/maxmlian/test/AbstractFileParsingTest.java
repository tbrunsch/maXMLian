package dd.kms.maxmlian.test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dd.kms.maxmlian.api.*;

@RunWith(Parameterized.class)
public abstract class AbstractFileParsingTest
{
	private final Path	xmlFile;

	public AbstractFileParsingTest(Path xmlFile) {
		this.xmlFile = xmlFile;
	}

	abstract void prepareTest(org.w3c.dom.Document domDocument);
	abstract int getNumberOfChildrenToParse(int depth);

	@Parameterized.Parameters(name = "{0}")
	public static List<Path> collectResourceXmlFiles() throws IOException, URISyntaxException {
		URL resourceDirectoryUrl = AbstractFileParsingTest.class.getResource("/");
		URI resourceDirectoryUri = resourceDirectoryUrl.toURI();
		Path resourceDirectory = Paths.get(resourceDirectoryUri);
		List<Path> resourceXmlFiles = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(resourceDirectory)) {
			for (Path resourcePath : stream) {
				if (Files.isRegularFile(resourcePath) && isXmlFile(resourcePath)) {
					resourceXmlFiles.add(resourcePath);
				}
			}
		}
		return resourceXmlFiles;
	}

	private static boolean isXmlFile(Path file) {
		return file.getFileName().toString().toLowerCase().endsWith(".xml");
	}

	@Test
	public void testParsingWholeFile() throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
		Document document = XmlParser.readXml(Files.newInputStream(xmlFile));

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		org.w3c.dom.Document domDocument = builder.parse(Files.newInputStream(xmlFile));

		prepareTest(domDocument);

		compareNodes(document, domDocument, 0);
	}

	private void compareNodes(Node node, org.w3c.dom.Node domNode, int depth) throws XMLStreamException {
		String name = domNode.getNodeName();
		Assert.assertEquals("Wrong node name", name, node.getNodeName());
		Assert.assertEquals("Wrong local name of node '" + name + "'", domNode.getLocalName(), node.getLocalName());
		Assert.assertEquals("Wrong prefix of node '" + name + "'", domNode.getPrefix(), node.getPrefix());
		Assert.assertEquals("Wrong namespace URI of node '" + name + "'", domNode.getNamespaceURI(), node.getNamespaceURI());

		short domNodeType = domNode.getNodeType();
		NodeType expectedNodeType = getNodeType(domNodeType);
		NodeType nodeType = node.getNodeType();
		Assert.assertEquals("Wrong type of node '" + name + "'", expectedNodeType, nodeType);

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

		int numChildrenToParse = getNumberOfChildrenToParse(depth);
		if (numChildrenToParse > 0) {
			Iterable<Node> children = node.getChildren();
			NodeList domChildren = domNode.getChildNodes();
			int numDomChildren = domChildren.getLength();
			int childIndex = 0;
			for (Node child : children) {
				Assert.assertTrue("Wrong number of children of node '" + name + "'", childIndex < numDomChildren);
				org.w3c.dom.Node domChild = domChildren.item(childIndex);
				compareNodes(child, domChild, depth + 1);
				childIndex++;
				if (childIndex >= numChildrenToParse) {
					break;
				}
			}
			if (childIndex < numChildrenToParse) {
				// this check must only be evaluated if the number of children has not been limited
				Assert.assertEquals("Wrong number of children of node '" + name + "'", numDomChildren, childIndex);
			}
		}
	}

	private void compareElements(Element element, org.w3c.dom.Element domElement, int depth) throws XMLStreamException {
		String name = element.getNodeName();
		Assert.assertEquals("Wrong tag name of element '" + name + "'", element.getTagName(), domElement.getTagName());

		int numChildrenToParse = getNumberOfChildrenToParse(depth);
		if (numChildrenToParse > 0) {
			Map<String, Attr> attributesByName = element.getAttributes();
			org.w3c.dom.NamedNodeMap domAttributes = domElement.getAttributes();
			Assert.assertEquals("Wrong number of attributes of element '" + name + "'", domAttributes.getLength(), attributesByName.size());
			int attributeIndex = 0;
			for (String attributeName : attributesByName.keySet()) {
				Attr attribute = attributesByName.get(attributeName);
				org.w3c.dom.Node domAttribute = domAttributes.getNamedItem(attributeName);
				Assert.assertNotNull("Wrong attribute name '" + attributeName + "'", domAttribute);
				compareNodes(attribute, domAttribute, depth + 1);
				attributeIndex++;
				if (attributeIndex >= numChildrenToParse) {
					break;
				}
			}
		}
	}

	private void compareAttributes(Attr attribute, org.w3c.dom.Attr domAttribute) {
		String name = domAttribute.getName();
		Assert.assertEquals("Wrong name of an attribute", name, attribute.getName());
		Assert.assertEquals("Wrong value of attribute '" + name + "'", domAttribute.getValue(), attribute.getValue());
		Assert.assertEquals("Wrong value of getSpecified() of attribute '" + name + "'", domAttribute.getSpecified(), attribute.getSpecified());
		Assert.assertEquals("Wrong value of isId() of attribute '" + name + "'", domAttribute.isId(), attribute.isId());
	}

	private void compareTexts(Text text, org.w3c.dom.Text domText) {
		String name = domText.getNodeName();
		Assert.assertEquals("Wrong data of text node '" + name + "'", domText.getData(), text.getData());
		Assert.assertEquals("Wrong value of isElementContextWhiteSpace() of text node '" + name + "'", domText.isElementContentWhitespace(), text.isElementContentWhitespace());
	}

	private void compareCDataSections(CDATASection cDataSection, org.w3c.dom.CDATASection domCDataSection) {
		String name = domCDataSection.getNodeName();
		Assert.assertEquals("Wrong data of CDATA section node '" + name + "'", domCDataSection.getData(), cDataSection.getData());
		Assert.assertEquals("Wrong value of isElementContextWhiteSpace() of CDATA section node '" + name + "'", domCDataSection.isElementContentWhitespace(), cDataSection.isElementContentWhitespace());
	}

	private void compareEntities(Entity entity, org.w3c.dom.Entity domEntity) {
		String name = entity.getNodeName();
		Assert.assertEquals("Wrong public ID of entity '" + name + "'", domEntity.getPublicId(), entity.getPublicId());
		Assert.assertEquals("Wrong system ID of entity '" + name + "'", domEntity.getSystemId(), entity.getSystemId());
		Assert.assertEquals("Wrong notation name of entity '" + name + "'", domEntity.getNotationName(), entity.getNotationName());
	}

	private void compareProcessingInstructions(ProcessingInstruction processingInstruction, org.w3c.dom.ProcessingInstruction domProcessingInstruction) {
		String name = domProcessingInstruction.getNodeName();
		Assert.assertEquals("Wrong data of processing instruction '" + name + "'", domProcessingInstruction.getData(), processingInstruction.getData());
		Assert.assertEquals("Wrong target of processing instruction '" + name + "'", domProcessingInstruction.getTarget(), processingInstruction.getTarget());
	}

	private void compareComments(Comment comment, org.w3c.dom.Comment domComment) {
		String name = domComment.getNodeName();
		Assert.assertEquals("Wrong data of comment '" + name + "'", domComment.getData(), comment.getData());
	}

	private void compareDocuments(Document document, org.w3c.dom.Document domDocument) {
		String name = domDocument.getNodeName();
		Assert.assertEquals("Wrong input encoding of document '" + name + "'", domDocument.getInputEncoding().toUpperCase(), document.getInputEncoding().toUpperCase());
		Assert.assertEquals("Wrong value of getXmlStandalone() of document '" + name + "'", domDocument.getXmlStandalone(), document.getXmlStandalone());
		Assert.assertEquals("Wrong XML version of document '" + name + "'", domDocument.getXmlVersion(), document.getXmlVersion());

		/*
		 * Do not test getDocumentElement() and getDoctype() because this will make document.getChildren()
		 * throw an exception because of streamed parsing. The document element and the document type will
		 * be compared nevertheless because they are part of the DOM tree.
		 */
	}

	private void compareDocumentTypes(DocumentType docType, org.w3c.dom.DocumentType domDocType, int depth) throws XMLStreamException {
		String name = domDocType.getName();
		Assert.assertEquals("Wrong name of document type '" + name + "'", domDocType.getName(), docType.getName());
		Assert.assertEquals("Wrong public ID of document type '" + name + "'", domDocType.getPublicId(), docType.getPublicId());
		Assert.assertEquals("Wrong system ID of document type '" + name + "'", domDocType.getSystemId(), docType.getSystemId());

		int numChildrenToParse = getNumberOfChildrenToParse(depth);

		if (numChildrenToParse > 0) {
			Map<String, Entity> entitiesByName = docType.getEntities();
			org.w3c.dom.NamedNodeMap domEntities = domDocType.getEntities();
			Assert.assertEquals("Wrong number of entities of document type '" + name + "'", domEntities.getLength(), entitiesByName.size());
			int entityIndex = 0;
			for (String entityName : entitiesByName.keySet()) {
				Entity entity = entitiesByName.get(entityName);
				org.w3c.dom.Node domEntity = domEntities.getNamedItem(entityName);
				Assert.assertNotNull("Wrong entity name '" + entityName + "'", domEntity);
				compareNodes(entity, domEntity, depth + 1);
				entityIndex++;
				if (entityIndex >= numChildrenToParse) {
					break;
				}
			}
		}

		if (numChildrenToParse > 0) {
			Map<String, Notation> notationsByName = docType.getNotations();
			org.w3c.dom.NamedNodeMap domNotations = domDocType.getNotations();
			Assert.assertEquals("Wrong number of notations of document type '" + name + "'", domNotations.getLength(), notationsByName.size());
			int notationIndex = 0;
			for (String notationName : notationsByName.keySet()) {
				Notation notation = notationsByName.get(notationName);
				org.w3c.dom.Node domNotation = domNotations.getNamedItem(notationName);
				Assert.assertNotNull("Wrong notation name '" + notationName + "'", domNotation);
				compareNodes(notation, domNotation, depth + 1);
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
		Assert.assertEquals("Wrong public ID of notation '" + name + "'", domNotation.getPublicId(), notation.getPublicId());
		Assert.assertEquals("Wrong system ID of notation '" + name + "'", domNotation.getSystemId(), notation.getSystemId());
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
