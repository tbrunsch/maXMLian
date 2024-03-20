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

	/**
	 * @return the concatenation of the texts of all text nodes in the tree rooted
	 * at this node, including this node.
	 */
	String getTextContent() throws XmlException;

	/**
	 * @return a stream of the texts of all text nodes in the tree rooted
	 * at this node, including this node.
	 */
	StringStream getTextContentStream();
}
