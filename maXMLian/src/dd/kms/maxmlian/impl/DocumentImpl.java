package dd.kms.maxmlian.impl;

import java.io.InputStream;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;

import dd.kms.maxmlian.api.*;

class DocumentImpl extends NodeImpl implements Document
{
	private String  inputEncoding;
	private boolean standalone;
	private String	xmlVersion;

	private boolean      retrievedChildren;
	private DocumentType docType;
	private Element      documentElement;

	DocumentImpl(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initializeFromStartDocument(StartDocument startDocument) {
		super.initialize();
		inputEncoding = startDocument.getCharacterEncodingScheme();
		standalone = startDocument.isStandalone();
		xmlVersion = startDocument.getVersion();
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
	public Iterable<Node> getChildNodes() throws XMLStreamException {
		Iterable<Node> children = super.getChildNodes();
		retrievedChildren = true;
		return children;
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
