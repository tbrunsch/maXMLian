package dd.kms.maxmlian.samples;

import dd.kms.maxmlian.api.*;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;

public class XhtmlAnalysisSample
{
	private static final String	XML	= "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">"
									+ "<html xmlns=\"http://www.w3.org/1999/xhtml\">"
									+	"<head>"
									+		"<title>XHTML Analysis Sample</title>"
									+	"</head>"
									+	"<body>"
									+		"<p>The objective of this sample is to print <b>all</b> bold text fragments.</p>"
									+		"<table>"
									+			"<tr><th>Header 1</th><th>Header 2</th></tr>"
									+			"<tr><td>nothing special</td><td>a <b>bold</b> value</td></tr>"
									+			"<tr><td colspan=\"2\">and <b>another</b> one</td></tr>"
									+			"<tr><td></td><td>a <b>bold text with <i>italic</i> content</b></td></tr>"
									+		"</table>"
									+	"</body>"
									+ "</html>";

	public static void main(String[] args) throws XmlException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(new ByteArrayInputStream(XML.getBytes()));
		printBoldTexts(document);
	}

	private static void printBoldTexts(Node node) throws XmlException {
		if (node.getNodeType() == NodeType.ELEMENT && "b".equals(node.getNodeName())) {
			System.out.println(node.getTextContent());
			return;
		}
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			printBoldTexts(child);
		}
	}
}
