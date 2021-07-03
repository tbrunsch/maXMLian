package dd.kms.maxmlian.api;

/**
 * This class is used to wrap checked exceptions where no checked exception is allowed to be used
 */
public class XmlException extends RuntimeException
{
	public XmlException(String message, Throwable cause) {
		super(message, cause);
	}
}
