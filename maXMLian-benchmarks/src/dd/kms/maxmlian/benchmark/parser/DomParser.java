package dd.kms.maxmlian.benchmark.parser;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class DomParser extends AbstractParser
{
	@Override
	void doParseXml(Path xmlFile) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();

		Document document = documentBuilder.parse(Files.newInputStream(xmlFile));
		documentCreationFinished();

		traverse(document.getDocumentElement());
	}

	private void traverse(Node node) {
		traversalProgress();

		short nodeType = node.getNodeType();
		if (nodeType == Node.ELEMENT_NODE) {
			NamedNodeMap attributes = node.getAttributes();
			int numAttributes = attributes.getLength();
			for (int i = 0; i < numAttributes; i++) {
				Node attribute = attributes.item(i);
				traverse(attribute);
			}
		}

		NodeList childNodes = node.getChildNodes();
		int numChildren = childNodes.getLength();
		for (int i = 0; i < numChildren; i++) {
			Node childNode = childNodes.item(i);
			traverse(childNode);
		}
	}
}
