package dd.kms.maxmlian.samples;

import dd.kms.maxmlian.api.*;

import java.io.ByteArrayInputStream;

public class IncorrectApiUsageDetectionSample
{
	private static final String	XML	= "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
									+ "<sample>"
									+ 	"<element name=\"element 1\">"
									+ 		"<element name=\"subelement 1\"/>"
									+ 		"<element name=\"subelement 2\"/>"
									+ 	"</element>"
									+ 	"<element name=\"element 2\">"
									+ 		"<element name=\"subelement 3\"/>"
									+ 		"<element name=\"subelement 4\"/>"
									+ 	"</element>"
									+ "</sample>";

	public static void main(String[] args) throws XmlException {
		System.out.println("Without instance reuse: Incorrect API usage will be detected");
		incorrectApiUsage(false);

		System.out.println();

		System.out.println("With instance reuse: Incorrect API usage cannot be detected");
		incorrectApiUsage(true);
	}

	private static void incorrectApiUsage(boolean reuseInstances) throws XmlException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance().reuseInstances(reuseInstances);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(new ByteArrayInputStream(XML.getBytes()));

		Element sample = document.getDocumentElement();
		Element element1 = (Element) sample.getFirstChild();
		Element element2 = (Element) element1.getNextSibling();

		if (element2 == element1) {
			System.out.println("Element instance has been reused");
		}

		/*
		 * Try to access children of element 1, which is not possible because the parser has already
		 * parsed until element 2.
		 */
		try {
			System.out.println("Subelements of element 1:");
			for (Node child = element1.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child.getNodeType() == NodeType.ELEMENT) {
					Attr nameAttribute = child.getAttributes().get("name");
					System.out.println(nameAttribute.getValue());
				}
			}
		} catch (XmlStateException e) {
			System.out.println("Caught XmlStateException: " + e.getMessage());
		}
	}
}
