package dd.kms.maxmlian.api;

import java.io.InputStream;

public interface DocumentBuilder
{
	Document parse(InputStream is) throws XmlException;
}
