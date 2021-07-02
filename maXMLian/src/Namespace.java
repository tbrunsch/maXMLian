public interface Namespace extends Attr
{
	/**
	 * @return the prefix or "" for default namespace declarations
	 */
	String getPrefix();

	/**
	 * @return the uri bound to the prefix of this namespace
	 */
	String getNamespaceURI();

	/**
	 * @return true if this attribute declares the default namespace
	 */
	boolean isDefaultNamespaceDeclaration();
}
