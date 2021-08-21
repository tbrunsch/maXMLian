package dd.kms.maxmlian.benchmark;

import dd.kms.maxmlian.benchmark.parser.*;
import dd.kms.maxmlian.impl.StAXParserType;

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
		PARSERS_BY_NAME.put(DUMMY_PARSER_NAME,							new MaXMLianParser(1));
		PARSERS_BY_NAME.put("Xerces parser (Cursor API)",				new StAXParserCursor(StAXParserType.XERCES));
		PARSERS_BY_NAME.put("Woodstox parser (Cursor API)",				new StAXParserCursor(StAXParserType.WOODSTOX));
		PARSERS_BY_NAME.put("Aalto XML parser (Cursor API)",			new StAXParserCursor(StAXParserType.AALTO));
		PARSERS_BY_NAME.put("Xerces parser (Iterator API)",				new StAXParserIterator(StAXParserType.XERCES));
		PARSERS_BY_NAME.put("Woodstox parser (Iterator API)",			new StAXParserIterator(StAXParserType.WOODSTOX));
		PARSERS_BY_NAME.put("Aalto XML parser (Iterator API)",			new StAXParserIterator(StAXParserType.AALTO));
		PARSERS_BY_NAME.put("maXMLian with immediate instance reuse",	new MaXMLianParser(1));
		PARSERS_BY_NAME.put("maXMLian with instance reuse delay 5",		new MaXMLianParser(5));
		PARSERS_BY_NAME.put("maXMLian without instance reuse",			new MaXMLianParser(Integer.MAX_VALUE));
		PARSERS_BY_NAME.put("DOM parser",								new DomParser());
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
