package dd.kms.maxmlian.api;

/**
 * This class is used to report errors when parsing the XML file.
 */
public class XmlException extends Exception
{
	public XmlException(String message) {
		super(message);
	}

	public XmlException(String message, Throwable cause) {
		super(message, cause);
	}
}
