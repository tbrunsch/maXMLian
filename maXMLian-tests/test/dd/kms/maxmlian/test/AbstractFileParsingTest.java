package dd.kms.maxmlian.test;

import dd.kms.maxmlian.api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

abstract class AbstractFileParsingTest
{
	/**
	 * The Xerces parser does not recognize whitespace content in elements correctly
	 * for reflections.xml when the file has Unix or Mac line breaks and activated
	 * coalescing (= normalization). We don't consider this critical, so we simply
	 * skip testing whether the element content consists of whitespaces in these
	 * special cases.
	 */
	private static final Predicate<ParameterizedFileParsingTest>	SKIP_ELEMENT_CONTENT_WHITESPACE_TEST_PREDICATE =
		test -> test.xmlFile.getFileName().toString().equals("reflections.xml")
				&& !test.considerOnlyChildElements
				&& test.xmlInputFactoryProvider == XmlInputFactoryProvider.XERCES
				&& test.lineBreakStyle == LineBreakStyle.UNIX || test.lineBreakStyle == LineBreakStyle.MAC;

	abstract void prepareTest(org.w3c.dom.Document domDocument, boolean considerOnlyChildElements);
	abstract int getNumberOfChildrenToParse(int depth);

	static List<Object[]> getParameters() throws IOException {
		List<Path> paths = TestUtils.getTestFiles();
		List<Boolean> namespaceAwarenessValues = Arrays.asList(false, true);
		List<Boolean> considerOnlyChildElementsValues = Arrays.asList(false, true);
		List<XmlInputFactoryProvider> inputFactoryProviders = Arrays.asList(
			XmlInputFactoryProvider.XERCES,
			XmlInputFactoryProvider.WOODSTOX
		);
		List<LineBreakStyle> lineBreakStyles = Arrays.asList(LineBreakStyle.WINDOWS, LineBreakStyle.UNIX, LineBreakStyle.MAC);
		return TestUtils.cartesianProduct(Arrays.asList(paths, namespaceAwarenessValues, considerOnlyChildElementsValues, inputFactoryProviders, lineBreakStyles))
			.stream()
			.map(List::toArray)
			.collect(Collectors.toList());
	}

	@ParameterizedTest(name = "{0}, namespace aware: {1}, consider only child elements: {2}, StAX parser: {3}, line breaks: {4}")
	@MethodSource("getParameters")
	void testParsingFile(Path xmlFile, boolean namespaceAware, boolean considerOnlyChildElements, XmlInputFactoryProvider xmlInputFactoryProvider, LineBreakStyle lineBreakStyle) throws IOException, ParserConfigurationException, SAXException, XmlException {
		ParameterizedFileParsingTest parameterizedFileParsingTest = new ParameterizedFileParsingTest(xmlFile, namespaceAware, considerOnlyChildElements, xmlInputFactoryProvider, lineBreakStyle);
		parameterizedFileParsingTest.compareXmlStructure();
	}

	private class ParameterizedFileParsingTest
	{
		private final Path						xmlFile;
		private final boolean					namespaceAware;
		private final boolean					considerOnlyChildElements;
		private final XmlInputFactoryProvider	xmlInputFactoryProvider;
		private final LineBreakStyle			lineBreakStyle;
		private final boolean					testElementContentWhitespace;

		ParameterizedFileParsingTest(Path xmlFile, boolean namespaceAware, boolean considerOnlyChildElements, XmlInputFactoryProvider xmlInputFactoryProvider, LineBreakStyle lineBreakStyle) {
			this.xmlFile = xmlFile;
			this.namespaceAware = namespaceAware;
			this.considerOnlyChildElements = considerOnlyChildElements;
			this.xmlInputFactoryProvider = xmlInputFactoryProvider;
			this.lineBreakStyle = lineBreakStyle;
			testElementContentWhitespace = !SKIP_ELEMENT_CONTENT_WHITESPACE_TEST_PREDICATE.test(this);
		}

		void compareXmlStructure() throws ParserConfigurationException, IOException, XmlException, SAXException {
			Optional<XMLInputFactory> optXmlInputFactory = xmlInputFactoryProvider.getXMLInputFactory();
			Assumptions.assumeTrue(optXmlInputFactory.isPresent(), "Cannot create XMLInputFactory " + xmlInputFactoryProvider);
			XMLInputFactory xmlInputFactory = optXmlInputFactory.get();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(xmlInputFactory);
			DocumentBuilder documentBuilder = factory
				.reuseInstances(true)
				.namespaceAware(namespaceAware)
				.normalize(true)
				.dtdSupport(DtdSupport.INTERNAL_AND_EXTERNAL)
				.newDocumentBuilder();
			try (InputStream stream1 = TestUtils.createInputStream(xmlFile, lineBreakStyle);
				 Document document = documentBuilder.parse(stream1)) {
				javax.xml.parsers.DocumentBuilderFactory domFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
				domFactory.setNamespaceAware(namespaceAware);
				domFactory.setValidating(false);

				javax.xml.parsers.DocumentBuilder builder = domFactory.newDocumentBuilder();
				try (InputStream stream2 = TestUtils.createInputStream(xmlFile, lineBreakStyle)) {
					org.w3c.dom.Document domDocument = builder.parse(stream2);

					prepareTest(domDocument, considerOnlyChildElements);

					compareNodesRecursively(document, domDocument, 0);
				}
			}
		}

		private void compareNodesRecursively(Node node, org.w3c.dom.Node domNode, int depth) throws XmlException {
			compareNodes(node, domNode, depth);

			String name = domNode.getNodeName();

			int numChildrenToParse = getNumberOfChildrenToParse(depth);
			if (numChildrenToParse > 0) {
				NodeList domChildren = domNode.getChildNodes();
				if (considerOnlyChildElements) {
					List<org.w3c.dom.Node> domElements = IntStream.range(0, domChildren.getLength())
						.mapToObj(domChildren::item)
						.filter(child -> child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
						.collect(Collectors.toList());
					int numDomElements = domElements.size();
					int childElementIndex = 0;
					for (Element childElement = node.getFirstChildElement(); childElement != null; childElement = childElement.getNextSiblingElement()) {
						Assertions.assertTrue(childElementIndex < numDomElements, "Wrong number of child elements of node '" + name + "'");
						org.w3c.dom.Node domChildElement = domElements.get(childElementIndex);
						compareNodesRecursively(childElement, domChildElement, depth + 1);
						childElementIndex++;
						if (childElementIndex >= numChildrenToParse) {
							break;
						}
					}
					if (childElementIndex < numChildrenToParse) {
						// this check must only be evaluated if the number of children has not been limited
						Assertions.assertEquals(numDomElements, childElementIndex, "Wrong number of child elements of node '" + name + "'");
					}
				} else {
					int numDomChildren = domChildren.getLength();
					int childIndex = 0;
					for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
						Assertions.assertTrue(childIndex < numDomChildren, "Wrong number of children of node '" + name + "'");
						org.w3c.dom.Node domChild = domChildren.item(childIndex);
						compareNodesRecursively(child, domChild, depth + 1);
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
		}

		private void compareNodes(Node node, org.w3c.dom.Node domNode, int depth) throws XmlException {
			String name = domNode.getNodeName();
			String prefix = domNode.getPrefix();
			Assertions.assertEquals(name, node.getNodeName(), "Wrong node name");
			Assertions.assertEquals(domNode.getLocalName(), node.getLocalName(), "Wrong local name of node '" + name + "'");
			Assertions.assertEquals(prefix, node.getPrefix(), "Wrong prefix of node '" + name + "'");
			Assertions.assertEquals(domNode.getNamespaceURI(), node.getNamespaceURI(), "Wrong namespace URI of node '" + name + "'");

			Node parent = node.getParentNode();
			org.w3c.dom.Node domParent = domNode.getParentNode();
			if (domParent == null) {
				if (parent != null) {
					Assertions.fail("Unexpected parent '" + parent.getNodeName() + "'");
				}
			} else {
				Assertions.assertNotNull(parent, "Expected parent '" + domParent.getNodeName() + "'");
				compareNodes(parent, domParent, depth - 1);
			}

			short domNodeType = domNode.getNodeType();
			NodeType expectedNodeType = TestUtils.getNodeType(domNodeType);
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
		}

		private void compareElements(Element element, org.w3c.dom.Element domElement, int depth) throws XmlException {
			String name = element.getNodeName();
			Assertions.assertEquals(element.getTagName(), domElement.getTagName(), "Wrong tag name of element '" + name + "'");

			int numChildrenToParse = getNumberOfChildrenToParse(depth);
			if (numChildrenToParse > 0) {
				NamedAttributeMap attributes = element.getAttributes();
				org.w3c.dom.NamedNodeMap domAttributes = domElement.getAttributes();
				int numAttributes = attributes.size();
				Assertions.assertEquals(domAttributes.getLength(), numAttributes, "Wrong number of attributes of element '" + name + "'");
				for (int attributeIndex = 0; attributeIndex < Math.min(numAttributes, numChildrenToParse); attributeIndex++) {
					Attr attribute = attributes.get(attributeIndex);
					String attributeName = attribute.getName();
					Assertions.assertEquals(attribute, attributes.get(attributeName), "Querying attribute '" + attributeName + "' by name failed");
					org.w3c.dom.Node domAttribute = domAttributes.getNamedItem(attributeName);
					Assertions.assertNotNull(domAttribute, "Wrong attribute name '" + attributeName + "'");
					compareNodesRecursively(attribute, domAttribute, depth + 1);
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
			if (testElementContentWhitespace) {
				Assertions.assertEquals(domText.isElementContentWhitespace(), text.isElementContentWhitespace(), "Wrong value of isElementContextWhiteSpace() of text node '" + name + "'");
			}
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

		private void compareDocumentTypes(DocumentType docType, org.w3c.dom.DocumentType domDocType, int depth) throws XmlException {
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
					compareNodesRecursively(entity, domEntity, depth + 1);
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
					compareNodesRecursively(notation, domNotation, depth + 1);
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
	}
}
