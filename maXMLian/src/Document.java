public interface Document extends Node
{
	DocumentType getDoctype();
	Element getDocumentElement();
	String getInputEncoding();
	boolean getXmlStandalone();
	String getXmlVersion();
}
