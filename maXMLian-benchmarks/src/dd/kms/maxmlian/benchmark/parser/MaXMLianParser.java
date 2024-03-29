package dd.kms.maxmlian.benchmark.parser;

import dd.kms.maxmlian.api.*;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class MaXMLianParser extends AbstractParser
{
	private final boolean	reuseInstances;

	public MaXMLianParser(boolean reuseInstances) {
		this.reuseInstances = reuseInstances;
	}

	@Override
	void doParseXml(Path xmlFile) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance().reuseInstances(reuseInstances);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();

		try (InputStream stream = Files.newInputStream(xmlFile);
			Document document = documentBuilder.parse(stream)) {
			documentCreationFinished();

			traverse(document.getDocumentElement());
		}
	}

	private void traverse(Node node) throws XmlException {
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

		for (Node childNode = node.getFirstChild(); childNode != null; childNode = childNode.getNextSibling()) {
			traverse(childNode);
		}
	}
}
