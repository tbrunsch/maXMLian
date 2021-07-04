package dd.kms.maxmlian.impl;

import java.io.InputStream;
import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;

import dd.kms.maxmlian.api.*;

// TODO: Check whether this class still must be public
public class DocumentImpl extends NodeImpl implements Document
{
	private String  inputEncoding;
	private boolean standalone;
	private String	xmlVersion;

	private boolean      retrievedChildren;
	private DocumentType docType;
	private Element      documentElement;

	public DocumentImpl(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initializeFromStartDocument(StartDocument startDocument) {
		super.initializePosition();
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
	public Iterable<Node> getChildren() throws XMLStreamException {
		Iterable<Node> children = super.getChildren();
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

	// TODO: Check whether this method must still be public
	public static Document createDocument(InputStream inputStream) throws XMLStreamException {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = factory.createXMLEventReader(inputStream);
		ExtendedXmlEventReader extendedReader = new ExtendedXmlEventReader(eventReader);
		NodeFactory nodeFactory = new NodeFactory(extendedReader);
		Node child = nodeFactory.readFirstChildNode();
		if (child.getNodeType() == NodeType.DOCUMENT) {
			return (Document) child;
		}
		throw new XMLStreamException("Unable to find document node in XML stream");
	}
}
