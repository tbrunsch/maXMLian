package dd.kms.maxmlian.benchmark.parser;

import dd.kms.maxmlian.api.*;

import javax.xml.stream.XMLStreamException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class MaXMLianParser extends AbstractParser
{
	private final int	reuseDelay;

	public MaXMLianParser(int reuseDelay) {
		this.reuseDelay = reuseDelay;
	}

	@Override
	void doParseXml(Path xmlFile) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.delayedInstanceReuse(reuseDelay);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();

		Document document = documentBuilder.parse(Files.newInputStream(xmlFile));
		documentCreationFinished();

		traverse(document.getDocumentElement());
	}

	private void traverse(Node node) throws XMLStreamException {
		traversalProgress();

		NodeType nodeType = node.getNodeType();
		if (nodeType == NodeType.ELEMENT) {
			Map<String, Attr> attributes = node.getAttributes();
			for (Attr attribute : attributes.values()) {
				traverse(attribute);
			}
		}

		Iterable<Node> childNodes = node.getChildNodes();
		for (Node childNode : childNodes) {
			traverse(childNode);
		}
	}
}
