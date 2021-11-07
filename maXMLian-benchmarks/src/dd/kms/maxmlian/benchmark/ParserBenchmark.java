package dd.kms.maxmlian.benchmark;

import dd.kms.maxmlian.benchmark.parser.*;
import dd.kms.maxmlian.impl.XMLInputFactoryProvider;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

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
		PARSERS_BY_NAME.put(DUMMY_PARSER_NAME,									new MaXMLianParserNonIterable(true));
		PARSERS_BY_NAME.put("Xerces (Cursor API)",								new StAXParserCursor(XMLInputFactoryProvider.XERCES));
		PARSERS_BY_NAME.put("Woodstox (Cursor API)",							new StAXParserCursor(XMLInputFactoryProvider.WOODSTOX));
		PARSERS_BY_NAME.put("Aalto (Cursor API)",								new StAXParserCursor(XMLInputFactoryProvider.AALTO));
		PARSERS_BY_NAME.put("Xerces (Iterator API)",							new StAXParserIterator(XMLInputFactoryProvider.XERCES));
		PARSERS_BY_NAME.put("Woodstox (Iterator API)",							new StAXParserIterator(XMLInputFactoryProvider.WOODSTOX));
		PARSERS_BY_NAME.put("Aalto (Iterator API)",								new StAXParserIterator(XMLInputFactoryProvider.AALTO));
		PARSERS_BY_NAME.put("maXMLian with instance reuse",						new MaXMLianParserNonIterable(true));
		PARSERS_BY_NAME.put("maXMLian without instance reuse",					new MaXMLianParserNonIterable(false));
		PARSERS_BY_NAME.put("maXMLian with instance reuse (Iterable style)",	new MaXMLianParserIterable(true));
		PARSERS_BY_NAME.put("maXMLian without instance reuse (Iterable style)",	new MaXMLianParserIterable(false));
		PARSERS_BY_NAME.put("DOM parser",										new DomParser());
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
