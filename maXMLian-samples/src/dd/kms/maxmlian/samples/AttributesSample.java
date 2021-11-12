package dd.kms.maxmlian.samples;

import dd.kms.maxmlian.api.*;

import java.io.ByteArrayInputStream;

public class AttributesSample
{
	private static final String	XML	= "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
									+ "<sample>"
									+ 	"<element name=\"element1\" value=\"4\" type=\"number\"/>"
									+ 	"<element name=\"element2\" value=\"char[4]@123\"/>"
									+ 	"<element name=\"element3\" value=\"text\" type=\"string\"/>"
									+ "</sample>";

	public static void main(String[] args) throws XmlException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(new ByteArrayInputStream(XML.getBytes()));
		Element sample = document.getDocumentElement();
		for (Node child = sample.getFirstChild(); child != null; child = child.getNextSibling()) {
			NamedAttributeMap attributes = child.getAttributes();
			int numAttributes = attributes.size();
			for (int i = 0; i < numAttributes; i++) {
				Attr attr = attributes.get(i);
				System.out.println(attr.getName() + "=" + attr.getValue());
			}
		}
	}
}
