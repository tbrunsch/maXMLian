package dd.kms.maxmlian.api;

public interface Node
{
	String getNodeName();
	String getNodeValue();
	NodeType getNodeType();

	Node getParentNode();
	Node getFirstChild() throws XmlException, XmlStateException;
	Node getNextSibling() throws XmlException, XmlStateException;
	Element getFirstChildElement() throws XmlException, XmlStateException;
	Element getNextSiblingElement() throws XmlException, XmlStateException;
	NamedAttributeMap getAttributes() throws XmlStateException;

	String getNamespaceURI();
	String getPrefix();
	String getLocalName();

	String getTextContent() throws XmlException, XmlStateException;

	/**
	 * This method essentially returns the same as {@link #getTextContent()}, but
	 * as a {@link StringStream}, i.e., one can query the text content junk wise.
	 */
	StringStream getTextContentStream();
}
