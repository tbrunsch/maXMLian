package dd.kms.maxmlian.test;

import dd.kms.maxmlian.api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TextContentTest
{
	static List<Object> getParameters() throws IOException {
		List<Path> paths = TestUtils.getTestFiles();
		List<Boolean> normalizeValues = Arrays.asList(false, true);
		List<Boolean> useTextContentStreamValues = Arrays.asList(false, true);
		List<Integer> subtreeHeightValues = Arrays.asList(0, 1, 2);
		List<XmlInputFactoryProvider> inputFactoryProviders = Arrays.asList(
			XmlInputFactoryProvider.XERCES,
			XmlInputFactoryProvider.WOODSTOX
		);
		return TestUtils.cartesianProduct(Arrays.asList(paths, normalizeValues, useTextContentStreamValues, subtreeHeightValues, inputFactoryProviders))
			.stream()
			.map(List::toArray)
			.collect(Collectors.toList());
	}

	@ParameterizedTest(name = "{0}, normalize: {1}, streamed text content: {2}, subtree height: {3}, StAX parser: {4}")
	@MethodSource("getParameters")
	public void testGetTextContent(Path xmlFile, boolean normalize, boolean useTextContentStream, int subtreeHeightToEvaluate, XmlInputFactoryProvider xmlInputFactoryProvider) throws IOException, SAXException, ParserConfigurationException, XmlException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(xmlInputFactoryProvider);
		DocumentBuilder documentBuilder = factory
			.reuseInstances(true)
			.normalize(normalize)
			.newDocumentBuilder();
		Document document = documentBuilder.parse(Files.newInputStream(xmlFile));

		javax.xml.parsers.DocumentBuilderFactory domFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		javax.xml.parsers.DocumentBuilder builder = domFactory.newDocumentBuilder();
		org.w3c.dom.Document domDocument = builder.parse(Files.newInputStream(xmlFile));

		compareNodes(document, domDocument, useTextContentStream, subtreeHeightToEvaluate);
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
			childIndex++;
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
			System.out.println(nextTextContentPart.length());
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
