package dd.kms.maxmlian.api;

public interface Document extends Node
{
	@Override
	default NodeType getNodeType() {
		return NodeType.DOCUMENT;
	}

	DocumentType getDoctype();
	Element getDocumentElement();
	String getXmlEncoding();
	boolean getXmlStandalone();
	String getXmlVersion();
}
