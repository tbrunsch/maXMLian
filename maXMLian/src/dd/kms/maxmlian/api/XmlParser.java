package dd.kms.maxmlian.api;

import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

public class XmlParser
{
	public static Document readXml(InputStream inputStream) throws XMLStreamException {
		return dd.kms.maxmlian.impl.DocumentImpl.createDocument(inputStream);
	}
}
