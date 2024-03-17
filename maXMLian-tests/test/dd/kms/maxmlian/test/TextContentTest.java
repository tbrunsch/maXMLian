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

public class TextContentTest
{
	private static final Path	TEST_FILE										= TestUtils.getResourceDirectory().resolve("mixed_content.xml");
	private static final String	NAME_OF_ELEMENTS_FOR_TEXT_CONTENT_COMPARISON	= "text";

	static List<Object> getParameters() {
		return Arrays.asList(
			XmlInputFactoryProvider.XERCES,
			XmlInputFactoryProvider.WOODSTOX,
			XmlInputFactoryProvider.AALTO
		);
	}

	@ParameterizedTest(name = "StAX parser: {0}")
	@MethodSource("getParameters")
	public void testGetTextContent(XmlInputFactoryProvider xmlInputFactoryProvider) throws IOException, SAXException, ParserConfigurationException, XmlException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(xmlInputFactoryProvider);
		DocumentBuilder documentBuilder = factory
			.reuseInstances(true)
			.normalize(true)
			.newDocumentBuilder();
		Document document = documentBuilder.parse(Files.newInputStream(TEST_FILE));

		javax.xml.parsers.DocumentBuilderFactory domFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		javax.xml.parsers.DocumentBuilder builder = domFactory.newDocumentBuilder();
		org.w3c.dom.Document domDocument = builder.parse(Files.newInputStream(TEST_FILE));

		compareNodes(document, domDocument);
	}

	private void compareNodes(Node node, org.w3c.dom.Node domNode) throws XmlException {
		String name = domNode.getNodeName();
		Assertions.assertEquals(name, node.getNodeName(), "Wrong node name");

		short domNodeType = domNode.getNodeType();
		NodeType expectedNodeType = TestUtils.getNodeType(domNodeType);
		NodeType nodeType = node.getNodeType();
		Assertions.assertEquals(expectedNodeType, nodeType, "Wrong type of node '" + name + "'");

		switch (nodeType) {
			case ELEMENT:
				compareElements((Element) node, (org.w3c.dom.Element) domNode);
				// element comparison individually controls whether to compare children or not
				return;
			case TEXT:
				compareTexts((Text) node, (org.w3c.dom.Text) domNode);
				break;
			case COMMENT:
				compareComments((Comment) node, (org.w3c.dom.Comment) domNode);
				break;
			default:
				break;
		}
		compareChildren(node, domNode);
	}

	private void compareElements(Element element, org.w3c.dom.Element domElement) throws XmlException {
		String name = element.getNodeName();
		Assertions.assertEquals(element.getTagName(), domElement.getTagName(), "Wrong tag name of element '" + name + "'");

		if (NAME_OF_ELEMENTS_FOR_TEXT_CONTENT_COMPARISON.equals(name)) {
			String textContent = element.getTextContent();
			String expectedTextContent = domElement.getTextContent();
			Assertions.assertEquals(expectedTextContent, textContent, "Wrong text content of element '" + name + "'");
		} else {
			/*
			 * Only compare children if the element's text content has not been evaluated because
			 * the children have already been parsed otherwise.
			 */
			compareChildren(element, domElement);
		}
	}

	private void compareTexts(Text text, org.w3c.dom.Text domText) {
		String name = domText.getNodeName();
		Assertions.assertEquals(domText.getData(), text.getData(), "Wrong data of text node '" + name + "'");
		Assertions.assertEquals(domText.isElementContentWhitespace(), text.isElementContentWhitespace(), "Wrong value of isElementContextWhiteSpace() of text node '" + name + "'");
	}

	private void compareComments(Comment comment, org.w3c.dom.Comment domComment) {
		String name = domComment.getNodeName();
		Assertions.assertEquals(domComment.getData(), comment.getData(), "Wrong data of comment '" + name + "'");
	}

	private void compareChildren(Node node, org.w3c.dom.Node domNode) throws XmlException {
		NodeList domChildren = domNode.getChildNodes();
		int numDomChildren = domChildren.getLength();
		int childIndex = 0;
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			Assertions.assertTrue(childIndex < numDomChildren, "Wrong number of children of node '" + domNode.getNodeName() + "'");
			org.w3c.dom.Node domChild = domChildren.item(childIndex);
			compareNodes(child, domChild);
			childIndex++;
		}
	}
}
