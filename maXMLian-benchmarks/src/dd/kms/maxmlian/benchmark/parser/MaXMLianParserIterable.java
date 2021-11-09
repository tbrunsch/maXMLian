package dd.kms.maxmlian.benchmark.parser;

import dd.kms.maxmlian.api.*;

import java.nio.file.Files;
import java.nio.file.Path;

public class MaXMLianParserIterable extends AbstractParser
{
	private final boolean	reuseInstances;

	public MaXMLianParserIterable(boolean reuseInstances) {
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

	private void traverse(Node node) throws XmlException {
		traversalProgress();

		NodeType nodeType = node.getNodeType();
		if (nodeType == NodeType.ELEMENT) {
			NamedAttributeMap attributes = node.getAttributes();
			for (Attr attribute : attributes) {
				traverse(attribute);
			}
		}

		Iterable<Node> childNodes = node.getChildNodes();
		for (Node childNode : childNodes) {
			traverse(childNode);
		}
	}
}
