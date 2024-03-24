package dd.kms.maxmlian.test;

import dd.kms.maxmlian.api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

public class CorrectApiUsageDetectionTest
{
	private static final String	XML	= "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
									+ "<root>"
									+	"some text"
									+	"<element name='elem1'>"
									+		"more text"
									+		"<subelement name='sub1'/>"
									+		"even more text"
									+		"<subelement name='sub2'>"
									+			"<child name='child1'/>"
									+			"text between children"
									+			"<child name='child2'/>"
									+		"</subelement>"
									+	"</element>"
									+	"<!-- some comment -->"
									+	"<element name='elem2'>"
									+		"<subelement name='sub3'/>"
									+	"</element>"
									+ "</root>";

	@Test
	public void getNextSiblingAfterGettingChildren() throws XmlException {
		Element firstElement = getFirstElement();
		checkName(firstElement, "elem1");

		Element firstChildElement = firstElement.getFirstChildElement();
		checkName(firstChildElement, "sub1");
		Element secondChildElement = firstChildElement.getNextSiblingElement();
		checkName(secondChildElement, "sub2");

		/*
		 * Navigation to second element must be possible even if we have already
		 * parsed some children of the first element. This is the main workflow
		 * for parsing XML files in depth.
		 */
		Element secondElement = firstElement.getNextSiblingElement();
		checkName(secondElement, "elem2");
	}

	@Test
	public void getNextSiblingAfterGettingTextContent() throws XmlException {
		Element firstElement = getFirstElement();
		checkName(firstElement, "elem1");

		String textContent = firstElement.getTextContent();
		Assertions.assertEquals("more texteven more texttext between children", textContent);

		/*
		 * After getting the text content, the internal parser will be at the end
		 * of the element. Getting the next sibling is still possible.
		 */
		Element secondElement = firstElement.getNextSiblingElement();
		checkName(secondElement, "elem2");
	}

	@Test
	public void getFirstChildTwice() throws XmlException {
		Element firstElement = getFirstElement();
		checkName(firstElement, "elem1");

		Node firstChild = firstElement.getFirstChild();
		Assertions.assertEquals(NodeType.TEXT, firstChild.getNodeType());

		// Must not access first child a second time
		Assertions.assertThrows(XmlStateException.class, firstElement::getFirstChild);
	}

	@Test
	public void getFirstChildElementTwice() throws XmlException {
		Element firstElement = getFirstElement();
		checkName(firstElement, "elem1");

		Element firstChildElement = firstElement.getFirstChildElement();
		checkName(firstChildElement, "sub1");

		// Must not access first child a second time
		Assertions.assertThrows(XmlStateException.class, firstElement::getFirstChildElement);
	}

	@Test
	public void getFirstChildElementAfterGettingNextSiblingElement() throws XmlException {
		Element firstElement = getFirstElement();
		checkName(firstElement, "elem1");

		Element secondElement = firstElement.getNextSiblingElement();
		checkName(secondElement, "elem2");

		// Must not access first child after parser has moved to next element
		Assertions.assertThrows(XmlStateException.class, firstElement::getFirstChildElement);
	}

	@Test
	public void getAttributesAfterGettingFirstChild() throws XmlException {
		Element firstElement = getFirstElement();
		/*
		 * Do not call checkName() on firstElement because this would initialize
		 * its attribute map. However, the goal of this test is to show that
		 * initializing the attribute map after the parser has progressed fails.
		 */

		Element firstChildElement = firstElement.getFirstChildElement();
		checkName(firstChildElement, "sub1");

		// Attributes are also read on demand, so you must not access them after the parser has moved further
		Assertions.assertThrows(XmlStateException.class, () -> firstElement.getAttribute("name"));
	}

	@Test
	public void getTextContentAfterGettingFirstChild() throws XmlException {
		Element firstElement = getFirstElement();
		checkName(firstElement, "elem1");

		Node firstChild = firstElement.getFirstChild();
		Assertions.assertEquals(NodeType.TEXT, firstChild.getNodeType());

		/*
		 * Text content is read on demand. If the parser has already continued parsing
		 * beyond the beginning of the element, then an exception will be thrown.
		 */
		Assertions.assertThrows(XmlStateException.class, firstElement::getTextContent);
	}

	@Test
	public void getNextTextContentJunkAfterGettingFirstChild() throws XmlException {
		Element firstElement = getFirstElement();
		checkName(firstElement, "elem1");

		Node firstChild = firstElement.getFirstChild();
		Assertions.assertEquals(NodeType.TEXT, firstChild.getNodeType());

		/*
		 * Creating the text content stream still works because nothing needs to
		 * be parsed for it.
		 */
		StringStream textContentStream = firstElement.getTextContentStream();

		/*
		 * Trying to read the next part of the text content fails because we have
		 * already continued parsing beyond the beginning.
		 */
		Assertions.assertThrows(XmlStateException.class, textContentStream::next);
	}

	private static Element getFirstElement() throws XmlException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(new ByteArrayInputStream(XML.getBytes()));
		Element sample = document.getDocumentElement();
		return sample.getFirstChildElement();
	}

	private static void checkName(Element element, String expectedName) {
		String elementName = element.getAttributes().get("name").getNodeValue();
		Assertions.assertEquals(expectedName, elementName, "Unexpected value of attribute 'name'");
	}
}
