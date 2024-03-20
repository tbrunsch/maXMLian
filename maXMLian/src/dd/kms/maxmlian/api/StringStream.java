package dd.kms.maxmlian.api;

public interface StringStream
{
	/**
	 * @return The next {@code String} or {@code null} if no further {@code String} is available.
	 *
	 * @throws XmlException
	 */
	String next() throws XmlException;
}
