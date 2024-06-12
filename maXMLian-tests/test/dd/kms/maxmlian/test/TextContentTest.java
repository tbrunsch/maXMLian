package dd.kms.maxmlian.test;

import dd.kms.maxmlian.api.*;
import org.junit.jupiter.api.Assertions;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TextContentTest
{
	/**
	 * The Xerces parser does not recognize whitespace content in elements correctly
	 * for reflections.xml when the file has Unix or Mac line breaks and activated
	 * coalescing (= normalization). We don't consider this critical, so we simply
	 * trim the text content in these special cases.
	 */
	private static final Predicate<ParameterizedTextContentTest>	TRIM_TEXT_CONTENT_TEST_PREDICATE =
		test -> test.xmlFile.getFileName().toString().equals("reflections.xml")
			&& test.subtreeHeightToEvaluate == 2
			&& test.normalize
			&& test.xmlInputFactoryProvider == XmlInputFactoryProvider.XERCES
			&& test.lineBreakStyle == LineBreakStyle.UNIX || test.lineBreakStyle == LineBreakStyle.MAC;

	static List<Object> getParameters() throws IOException {
		List<Path> paths = TestUtils.getTestFiles();
		List<Boolean> normalizeValues = Arrays.asList(false, true);
		List<Boolean> useTextContentStreamValues = Arrays.asList(false, true);
		List<Integer> subtreeHeightValues = Arrays.asList(0, 1, 2);
		List<XmlInputFactoryProvider> inputFactoryProviders = Arrays.asList(
			XmlInputFactoryProvider.XERCES,
			XmlInputFactoryProvider.WOODSTOX
		);
		List<LineBreakStyle> lineBreakStyles = Arrays.asList(LineBreakStyle.WINDOWS, LineBreakStyle.UNIX, LineBreakStyle.MAC);

		return TestUtils.cartesianProduct(Arrays.asList(paths, normalizeValues, useTextContentStreamValues, subtreeHeightValues, inputFactoryProviders, lineBreakStyles))
			.stream()
			.map(List::toArray)
			.collect(Collectors.toList());
	}

	@ParameterizedTest(name = "{0}, normalize: {1}, streamed text content: {2}, subtree height: {3}, StAX parser: {4}, line breaks: {5}")
	@MethodSource("getParameters")
	public void testGetTextContent(Path xmlFile, boolean normalize, boolean useTextContentStream, int subtreeHeightToEvaluate, XmlInputFactoryProvider xmlInputFactoryProvider, LineBreakStyle lineBreakStyle) throws IOException, SAXException, ParserConfigurationException, XmlException {
		ParameterizedTextContentTest parameterizedTextContentTest = new ParameterizedTextContentTest(xmlFile, normalize, useTextContentStream, subtreeHeightToEvaluate, xmlInputFactoryProvider, lineBreakStyle);
		parameterizedTextContentTest.compareTextContent();
	}

	private static class ParameterizedTextContentTest
	{
		private final Path						xmlFile;
		private final boolean					normalize;
		private final boolean					useTextContentStream;
		private final int						subtreeHeightToEvaluate;
		private final XmlInputFactoryProvider	xmlInputFactoryProvider;
		private final LineBreakStyle			lineBreakStyle;
		private final boolean					trimTextContent;

		private ParameterizedTextContentTest(Path xmlFile, boolean normalize, boolean useTextContentStream, int subtreeHeightToEvaluate, XmlInputFactoryProvider xmlInputFactoryProvider, LineBreakStyle lineBreakStyle) {
			this.xmlFile = xmlFile;
			this.normalize = normalize;
			this.useTextContentStream = useTextContentStream;
			this.subtreeHeightToEvaluate = subtreeHeightToEvaluate;
			this.xmlInputFactoryProvider = xmlInputFactoryProvider;
			this.lineBreakStyle = lineBreakStyle;
			this.trimTextContent = TRIM_TEXT_CONTENT_TEST_PREDICATE.test(this);
		}

		void compareTextContent() throws IOException, SAXException, ParserConfigurationException, XmlException {
			XMLInputFactory xmlInputFactory = xmlInputFactoryProvider.getXMLInputFactory().get();
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(xmlInputFactory);
			DocumentBuilder documentBuilder = factory
				.reuseInstances(true)
				.normalize(normalize)
				.dtdSupport(DtdSupport.INTERNAL_AND_EXTERNAL)
				.newDocumentBuilder();

			try (InputStream stream1 = TestUtils.createInputStream(xmlFile, lineBreakStyle);
				 Document document = documentBuilder.parse(stream1)) {

				javax.xml.parsers.DocumentBuilderFactory domFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
				domFactory.setNamespaceAware(true);
				javax.xml.parsers.DocumentBuilder builder = domFactory.newDocumentBuilder();
				try (InputStream stream2 = TestUtils.createInputStream(xmlFile, lineBreakStyle)) {
					org.w3c.dom.Document domDocument = builder.parse(stream2);
					if (normalize) {
						domDocument.normalize();
					}

					compareNodes(document, domDocument, useTextContentStream, subtreeHeightToEvaluate);
				}
			}
		}

		private void compareNodes(Node node, org.w3c.dom.Node domNode, boolean useTextContentStream, int subtreeHeightToEvaluate) throws XmlException {
			String name = domNode.getNodeName();
			Assertions.assertEquals(name, node.getNodeName(), "Wrong node name");

			short domNodeType = domNode.getNodeType();
			NodeType expectedNodeType = TestUtils.getNodeType(domNodeType);
			NodeType nodeType = node.getNodeType();
			Assertions.assertEquals(expectedNodeType, nodeType, "Wrong type of node '" + name + "'");

			if (nodeType == NodeType.ELEMENT) {
				compareElements((Element) node, (org.w3c.dom.Element) domNode, useTextContentStream, subtreeHeightToEvaluate);
			} else {
				compareChildren(node, domNode, useTextContentStream, subtreeHeightToEvaluate);
			}
		}

		private void compareElements(Element element, org.w3c.dom.Element domElement, boolean useTextContentStream, int subtreeHeightToEvaluate) throws XmlException {
			String name = element.getNodeName();
			Assertions.assertEquals(element.getTagName(), domElement.getTagName(), "Wrong tag name of element '" + name + "'");

			int subtreeHeight = getTreeHeight(domElement);

			if (subtreeHeight == subtreeHeightToEvaluate) {
				String textContent = getTextContent(element, useTextContentStream);
				String expectedTextContent = domElement.getTextContent();
				if (trimTextContent) {
					textContent = textContent.trim();
					expectedTextContent = expectedTextContent.trim();
				}
				Assertions.assertEquals(expectedTextContent, textContent, "Wrong text content of element '" + name + "'");
			} else {
				/*
				 * Only compare children if the element's text content has not been evaluated because
				 * the children have already been parsed otherwise.
				 */
				compareChildren(element, domElement, useTextContentStream, subtreeHeightToEvaluate);
			}
		}

		private void compareChildren(Node node, org.w3c.dom.Node domNode, boolean useTextContentStream, int subtreeHeightToEvaluate) throws XmlException {
			NodeList domChildren = domNode.getChildNodes();
			int numDomChildren = domChildren.getLength();
			int childIndex = 0;
			for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
				switch (child.getNodeType()) {
					case TEXT:
					case CDATA_SECTION:
					case COMMENT:
						/*
						 * Don't compare text nodes because without normalization it is not
						 * specified how many adjacent text nodes we will get.
						 */
						continue;
					default:
						break;
				}
				org.w3c.dom.Node domChild;
				do {
					Assertions.assertTrue(childIndex < numDomChildren, "Wrong number of children of node '" + domNode.getNodeName() + "'");
					domChild = domChildren.item(childIndex);
					childIndex++;
				} while (domChild instanceof org.w3c.dom.CharacterData);
				compareNodes(child, domChild, useTextContentStream, subtreeHeightToEvaluate);
			}
		}

		private String getTextContent(Node node, boolean useStream) throws XmlException {
			if (!useStream) {
				return node.getTextContent();
			}
			StringStream textContentStream = node.getTextContentStream();
			StringBuilder builder = new StringBuilder();
			String nextTextContentPart;
			while ((nextTextContentPart = textContentStream.next()) != null) {
				builder.append(nextTextContentPart);
			}
			return builder.toString();
		}

		private int getTreeHeight(org.w3c.dom.Element root) {
			NodeList children = root.getChildNodes();
			int numChildren = children.getLength();
			int height = 0;
			for (int i = 0; i < numChildren; i++) {
				org.w3c.dom.Node child = children.item(i);
				if (child.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
					int subtreeHeight = getTreeHeight((org.w3c.dom.Element) child);
					height = Math.max(height, 1 + subtreeHeight);
				}
			}
			return height;
		}
	}
}
