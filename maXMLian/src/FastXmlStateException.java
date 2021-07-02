/**
 * This exception indicates that the API user has tried to use the API in an unsupported way.
 */
public class FastXmlStateException extends RuntimeException
{
	FastXmlStateException(String message) {
		super(message);
	}

	FastXmlStateException(String message, Throwable cause) {
		super(message, cause);
	}
}
