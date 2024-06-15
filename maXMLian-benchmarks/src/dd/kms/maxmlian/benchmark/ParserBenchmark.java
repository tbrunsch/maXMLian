package dd.kms.maxmlian.benchmark;

import dd.kms.maxmlian.benchmark.parser.*;
import dd.kms.maxmlian.api.XmlInputFactoryProvider;

import javax.xml.stream.XMLInputFactory;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ParserBenchmark
{
	/**
	 * We test a dummy parser before the relevant parsers to reduce effects like caching files that
	 * could bias the results, giving the first parser a disadvantage.
	 */
	private static final String					DUMMY_PARSER_NAME	= "Dummy parser";
	private static final Map<String, Parser>	PARSERS_BY_NAME		= new LinkedHashMap<>();

	static {
		// Register parsers
		PARSERS_BY_NAME.put(DUMMY_PARSER_NAME,					new MaXMLianParser(true));
		registerStAXParserCursor("Xerces (Cursor API)",			XmlInputFactoryProvider.XERCES);
		registerStAXParserCursor("Woodstox (Cursor API)",		XmlInputFactoryProvider.WOODSTOX);
		registerStAXParserCursor("Aalto (Cursor API)",			XmlInputFactoryProvider.AALTO);
		registerStAXParserIterator("Xerces (Iterator API)",		XmlInputFactoryProvider.XERCES);
		registerStAXParserIterator("Woodstox (Iterator API)",	XmlInputFactoryProvider.WOODSTOX);
		registerStAXParserIterator("Aalto (Iterator API)",		XmlInputFactoryProvider.AALTO);
		PARSERS_BY_NAME.put("maXMLian with instance reuse",		new MaXMLianParser(true));
		PARSERS_BY_NAME.put("maXMLian without instance reuse",	new MaXMLianParser(false));
		PARSERS_BY_NAME.put("DOM parser",						new DomParser());
	}

	private static void registerStAXParserCursor(String parserName, XmlInputFactoryProvider xmlInputFactoryProvider) {
		Optional<XMLInputFactory> dummyFactory = xmlInputFactoryProvider.getXMLInputFactory();
		if (!dummyFactory.isPresent()) {
			System.out.println("Skipping benchmark '" + parserName + "' because the '" + xmlInputFactoryProvider + "' StAX parser cannot be generated");
			return;
		}
		PARSERS_BY_NAME.put(parserName, new StAXParserCursor(xmlInputFactoryProvider));
	}

	private static void registerStAXParserIterator(String parserName, XmlInputFactoryProvider xmlInputFactoryProvider) {
		Optional<XMLInputFactory> dummyFactory = xmlInputFactoryProvider.getXMLInputFactory();
		if (!dummyFactory.isPresent()) {
			System.out.println("Skipping benchmark '" + parserName + "' because the '" + xmlInputFactoryProvider + "' StAX parser cannot be generated");
			return;
		}
		PARSERS_BY_NAME.put(parserName, new StAXParserIterator(xmlInputFactoryProvider));
	}

	private final int	numTrials;
	private final int	numMemoryChecksDuringTraversal;

	ParserBenchmark(int numTrials, int numMemoryChecksDuringTraversal) {
		this.numTrials = numTrials;
		this.numMemoryChecksDuringTraversal = numMemoryChecksDuringTraversal;
	}

	Map<String, SingleParserBenchmarkResult> performBenchmark(Path xmlFile) throws Exception {
		Map<String, SingleParserBenchmarkResult> resultsByName = new LinkedHashMap<>();
		for (Map.Entry<String, Parser> entry : PARSERS_BY_NAME.entrySet()) {
			String parserName = entry.getKey();
			Parser parser = entry.getValue();
			boolean isDummyBenchmark = DUMMY_PARSER_NAME.equals(parserName);

			if (isDummyBenchmark) {
				System.out.println("Initial dummy benchmark to minimize bias caused by hardware or virtual machine optimizations...");
			} else {
				System.out.println("Benchmarking " + parserName + "...");
			}

			SingleParserBenchmark singleParserBenchmark = new SingleParserBenchmark(xmlFile, numTrials, numMemoryChecksDuringTraversal);
			SingleParserBenchmarkResult singleParserBenchmarkResult = singleParserBenchmark.performBenchmark(parser);

			if (!isDummyBenchmark) {
				resultsByName.put(parserName, singleParserBenchmarkResult);
			}
		}
		return resultsByName;
	}
}
