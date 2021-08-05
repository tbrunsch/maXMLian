package dd.kms.maxmlian.api;

/**
 * This exception indicates that the API user has tried to use the API in an unsupported way.
 */
public class XmlStateException extends RuntimeException
{
	public XmlStateException(String message) {
		super(message);
	}
}
