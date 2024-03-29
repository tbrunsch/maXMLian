package dd.kms.maxmlian.samples;

import dd.kms.maxmlian.api.*;

import java.io.ByteArrayInputStream;

public class TextContentStreamSample
{
	private static final String	XML	= "<?xml version=\"1.0\" encoding=\"utf-8\" ?>"
		+ "<sample>"
		+	"part1"
		+ 	"<element>"
		+		"part2"
		+	"</element>"
		+	"part3a\n"
		+	"part3b\n"
		+	"part3c"
		+ 	"<element>"
		+		"part4"
		+	"</element>"
		+	"part5"
		+ "</sample>";

	public static void main(String[] args) throws XmlException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		try (Document document = documentBuilder.parse(new ByteArrayInputStream(XML.getBytes()))) {
			Element sample = document.getDocumentElement();
			StringStream textContentStream = sample.getTextContentStream();
			int i = 1;
			String textContentPart;
			while ((textContentPart = textContentStream.next()) != null) {
				System.out.println("Junk " + i++ + ": " + textContentPart);
			}
		}
	}
}
