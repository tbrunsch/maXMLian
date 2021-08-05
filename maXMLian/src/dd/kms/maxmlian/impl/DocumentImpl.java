package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Document;
import dd.kms.maxmlian.api.DocumentType;
import dd.kms.maxmlian.api.Element;
import dd.kms.maxmlian.api.Node;

import java.util.Iterator;

class DocumentImpl extends NodeImpl implements Document
{
	private String 			inputEncoding;
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
		this.inputEncoding = characterEncodingScheme;
		this.standalone = standalone;
		this.xmlVersion = version;
	}

	@Override
	public DocumentType getDoctype() {
		if (!retrievedChildren) {
			retrieveDocumentTypeAndDocumentElement();
		}
		return docType;
	}

	@Override
	public Element getDocumentElement() {
		if (!retrievedChildren) {
			retrieveDocumentTypeAndDocumentElement();
		}
		return documentElement;
	}

	private void retrieveDocumentTypeAndDocumentElement() {
		try {
			Iterator<Node> childIterator = iterator();
			while (childIterator.hasNext()) {
				Node child = childIterator.next();
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
	public Iterable<Node> getChildNodes() {
		return this;
	}

	@Override
	public Node getNextSibling() {
		return null;
	}

	@Override
	public String getInputEncoding() {
		return inputEncoding;
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
}
