package dd.kms.maxmlian.api;

import java.util.Map;

import javax.xml.stream.XMLStreamException;

public interface Node
{
	String getNodeName();
	String getNodeValue();
	NodeType getNodeType();

	/**
	 * This method returns the child nodes of the current node. It must not be called if the XML file has
	 * already parsed beyond the opening tag of that element. If so, an exception is thrown.
	 *
	 * @return an {@link Iterable} over the child nodes
	 * @throws XmlStateException if the XML file has already be parsed beyond the
	 */
	Iterable<Node> getChildren() throws XMLStreamException;
	Node getFirstChild() throws XMLStreamException;
	Node getNextSibling();
	Map<String, Attr> getAttributes();

	String getNamespaceURI();
	String getPrefix();
	String getLocalName();

	/**
	 * @return the concatenation of the texts of all text nodes in the tree rooted
	 * at this node, including this node. The tree is traversed in preorder.
	 */
	String getTextContent() throws XMLStreamException;
//	boolean isDefaultNamespace(String namespaceURI);
}
