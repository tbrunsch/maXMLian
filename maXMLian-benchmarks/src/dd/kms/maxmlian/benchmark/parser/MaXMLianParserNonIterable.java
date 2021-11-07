package dd.kms.maxmlian.benchmark.parser;

import dd.kms.maxmlian.api.*;

import javax.xml.stream.XMLStreamException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MaXMLianParserNonIterable extends AbstractParser
{
	private final boolean	reuseInstances;

	public MaXMLianParserNonIterable(boolean reuseInstances) {
		this.reuseInstances = reuseInstances;
	}

	@Override
	void doParseXml(Path xmlFile) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance().reuseInstances(reuseInstances);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();

		Document document = documentBuilder.parse(Files.newInputStream(xmlFile));
		documentCreationFinished();

		traverse(document.getDocumentElement());
	}

	private void traverse(Node node) throws XMLStreamException {
		traversalProgress();

		NodeType nodeType = node.getNodeType();
		if (nodeType == NodeType.ELEMENT) {
			NamedAttributeMap attributes = node.getAttributes();
			int numAttributes = attributes.size();
			for (int i = 0; i < numAttributes; i++) {
				Attr attribute = attributes.get(i);
				traverse(attribute);
			}
		}

		Node childNode = node.getFirstChild();
		while (childNode != null) {
			traverse(childNode);
			childNode = childNode.getNextSibling();
		}
	}
}