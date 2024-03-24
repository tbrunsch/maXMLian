package dd.kms.maxmlian.api;

public interface Element extends Node
{
	@Override
	default NodeType getNodeType() {
		return NodeType.ELEMENT;
	}

	/**
	 * @return the fully qualified name of the element
	 */
	String getTagName();

	/**
	 * @return the value (as String) of the attribute with the given fully qualified name or null if no such attribute exists
	 */
	String getAttribute(String qName) throws XmlStateException;

	/**
	 * @return true if this element contains an attribute whose fully qualified name matches the specified name
	 */
	boolean hasAttribute(String qName) throws XmlStateException;
}
