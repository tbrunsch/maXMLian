package dd.kms.maxmlian.benchmark.parser;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

public class StAXParser extends AbstractParser
{
	@Override
	void doParseXml(Path xmlFile) throws Exception {
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = factory.createXMLEventReader(Files.newInputStream(xmlFile));
		documentCreationFinished();

		while (eventReader.hasNext()) {
			traversalProgress();
			XMLEvent event = eventReader.nextEvent();
			int eventType = event.getEventType();
			if (eventType == XMLEvent.START_ELEMENT) {
				StartElement startElement = event.asStartElement();
				Iterator attributeIter = startElement.getAttributes();
				while (attributeIter.hasNext()) {
					attributeIter.next();
					traversalProgress();
				}
				Iterator namespaceIter = startElement.getNamespaces();
				while (namespaceIter.hasNext()) {
					namespaceIter.next();
					traversalProgress();
				}
			}
		}
	}
}
