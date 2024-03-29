package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.*;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

class DocumentImpl extends NodeImpl implements Document
{
	private String			xmlEncoding;
	private boolean			standalone;
	private String			xmlVersion;

	private boolean			retrievedChildren;
	private DocumentType	docType;
	private Element			documentElement;

	DocumentImpl(ExtendedXmlStreamReader streamReader, NodeFactory nodeFactory) {
		super(streamReader, nodeFactory);
	}

	void initialize(String characterEncodingScheme, boolean standalone, String version) {
		super.initialize();
		this.xmlEncoding = characterEncodingScheme;
		this.standalone = standalone;
		this.xmlVersion = version;
	}

	@Override
	public DocumentType getDoctype() throws XmlException {
		if (!retrievedChildren) {
			retrieveDocumentTypeAndDocumentElement();
		}
		return docType;
	}

	@Override
	public Element getDocumentElement() throws XmlException {
		if (!retrievedChildren) {
			retrieveDocumentTypeAndDocumentElement();
		}
		return documentElement;
	}

	private void retrieveDocumentTypeAndDocumentElement() throws XmlException {
		try {
			for (Node child = getFirstChild(); child != null; child = child.getNextSibling()) {
				switch (child.getNodeType()) {
					case DOCUMENT_TYPE:
						docType = (DocumentType) child;
						break;
					case ELEMENT:
						documentElement = (Element) child;
						// do not read further children; otherwise, the whole document element will be parsed
						return;
					default:
						break;
				}
			}
		} finally {
			retrievedChildren = true;
		}
	}

	@Override
	public Node getParentNode() {
		return null;
	}

	@Override
	public NodeImpl getNextSibling() {
		return null;
	}

	@Override
	public String getXmlEncoding() {
		return xmlEncoding;
	}

	@Override
	public boolean getXmlStandalone() {
		return standalone;
	}

	@Override
	public String getXmlVersion() {
		return xmlVersion != null ? xmlVersion : "1.0";
	}

	@Override
	public String getNodeName() {
		return "#document";
	}

	@Override
	public void close() throws XmlException {
		try {
			XMLStreamReader reader = getReader();
			reader.close();
		} catch (XMLStreamException e) {
			throw new XmlException("Error closing the internal XMLStreamReader: " + e, e);
		}
	}
}
