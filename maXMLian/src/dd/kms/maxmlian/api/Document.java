package dd.kms.maxmlian.api;

public interface Document extends Node
{
	@Override
	default NodeType getNodeType() {
		return NodeType.DOCUMENT;
	}

	DocumentType getDoctype();
	Element getDocumentElement();
	String getInputEncoding();
	boolean getXmlStandalone();
	String getXmlVersion();
}
