/**
 * This class is used to wrap checked exceptions where no checked exception is allowed to be used
 */
public class FastXmlException extends RuntimeException
{
	FastXmlException(String message, Throwable cause) {
		super(message, cause);
	}
}
