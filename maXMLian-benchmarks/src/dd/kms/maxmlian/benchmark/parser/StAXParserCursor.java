package dd.kms.maxmlian.benchmark.parser;

import dd.kms.maxmlian.impl.XMLInputFactoryProvider;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.nio.file.Files;
import java.nio.file.Path;

public class StAXParserCursor extends AbstractParser
{
	private final XMLInputFactoryProvider	xmlInputFactoryProvider;

	public StAXParserCursor(XMLInputFactoryProvider xmlInputFactoryProvider) {
		this.xmlInputFactoryProvider = xmlInputFactoryProvider;
	}

	@Override
	void doParseXml(Path xmlFile) throws Exception {
		XMLInputFactory factory = xmlInputFactoryProvider.getXMLInputFactory().get();
		XMLStreamReader streamReader = factory.createXMLStreamReader(Files.newInputStream(xmlFile));
		documentCreationFinished();

		while (streamReader.hasNext()) {
			traversalProgress();
			streamReader.next();

			int eventType = streamReader.getEventType();
			if (eventType == XMLEvent.START_ELEMENT) {
				int numAttributes = streamReader.getAttributeCount();
				for (int i = 0; i < numAttributes; i++) {
					streamReader.getAttributeName(i);
					streamReader.getAttributeValue(i);
					traversalProgress();
				}
				int numNamespaces = streamReader.getNamespaceCount();
				for (int i = 0; i < numNamespaces; i++) {
					streamReader.getNamespaceURI(i);
					traversalProgress();
				}
			}
		}
	}
}
