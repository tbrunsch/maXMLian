package dd.kms.maxmlian.api;

import javax.xml.stream.XMLStreamException;
import java.io.InputStream;

public interface DocumentBuilder
{
	Document parse(InputStream is) throws XMLStreamException;
}
