import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

public class FastXmlParser
{
	public static Document readXml(InputStream inputStream) throws XMLStreamException {
		return DocumentImpl.createDocument(inputStream);
	}
}
