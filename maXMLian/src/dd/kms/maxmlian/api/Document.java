package dd.kms.maxmlian.api;

public interface Document extends Node
{
	@Override
	default NodeType getNodeType() {
		return NodeType.DOCUMENT;
	}

	DocumentType getDoctype() throws XmlException;
	Element getDocumentElement() throws XmlException;
	String getXmlEncoding();
	boolean getXmlStandalone();
	String getXmlVersion();
}
