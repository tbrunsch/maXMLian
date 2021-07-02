import java.util.Collection;

public interface Element extends Node
{
	/**
	 * @return the fully qualified name of the element
	 */
	String getTagName();

	/**
	 * @return the value (as String) of the attribute with the given fully qualified name or null if no such attribute exists
	 */
	String getAttribute(String qName);

	/**
	 * @return true if this element contains an attribute whose fully qualified name matches the specified name
	 */
	boolean hasAttribute(String qName);
}
