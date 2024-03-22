package dd.kms.maxmlian.api;

public interface Node
{
	String getNodeName();
	String getNodeValue();
	NodeType getNodeType();

	Node getParentNode();
	Node getFirstChild() throws XmlException;
	Node getNextSibling() throws XmlException;
	Element getFirstChildElement() throws XmlException;
	Element getNextSiblingElement() throws XmlException;
	NamedAttributeMap getAttributes();

	String getNamespaceURI();
	String getPrefix();
	String getLocalName();

	String getTextContent() throws XmlException;

	/**
	 * This method essentially returns the same as {@link #getTextContent()}, but
	 * as a {@link StringStream}, i.e., one can query the text content junk wise.
	 */
	StringStream getTextContentStream();
}
