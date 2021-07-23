package dd.kms.maxmlian.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

public class ParserBenchmarkSuite
{
	private static final int			NUM_TRIALS							= 3;
	private static final int			NUM_MEMORY_CHECKS_DURING_TRAVERSAL	= 10;

	private static final double			MILLIS_TO_SECONDS_FACTOR			= 1/1000.0;
	private static final double			BYTES_TO_MEGABYTES_FACTOR			= 1.0/(1 << 20);

	private static final DecimalFormat	DECIMAL_FORMAT						= new DecimalFormat("0.00##", new DecimalFormatSymbols(Locale.US));

	private static final Map<String, ToDoubleFunction<LongValueStatistics>>	STATISTICAL_VALUE_EXTRACTORS_BY_NAME	= new LinkedHashMap<>();

	static {
		STATISTICAL_VALUE_EXTRACTORS_BY_NAME.put("min", LongValueStatistics::getMinValue);
		STATISTICAL_VALUE_EXTRACTORS_BY_NAME.put("avg", LongValueStatistics::getAverageValue);
		STATISTICAL_VALUE_EXTRACTORS_BY_NAME.put("max", LongValueStatistics::getMaxValue);
	}

	/**
	 * @param args	An array containing the required benchmark parameters.<br/>
	 *              {@code args[0]} contains the full path of the benchmark file. You can use the {@code LargeXmlFileGenerator} to generate such a file.<br/>
	 *              {@code args[1]} contains the full path of the CSV file the benchmark results will be written to. This file must not exist.
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 2) {
			throw new IllegalArgumentException("There must be exactly 2 arguments: the full path of the benchmark XML file (does not need to exist) and the full path of the benchmark result file (must not exist)");
		}
		String benchmarkResultFilePath = args[1];
		Path benchmarkResultFile = Paths.get(benchmarkResultFilePath);
		if (Files.exists(benchmarkResultFile)) {
			throw new IllegalArgumentException("The benchmark result file must not exist");
		}

		String benchmarkXmlFilePath = args[0];
		Path benchmarkXmlFile = Paths.get(benchmarkXmlFilePath);
		if (!Files.exists(benchmarkXmlFile)) {
			throw new IllegalArgumentException("The benchmark XML file is does not exist");
		}
		if (!Files.isRegularFile(benchmarkXmlFile)) {
			throw new IllegalArgumentException("The benchmark XML file is no regular file");
		}

		ParserBenchmark parserBenchmark = new ParserBenchmark(NUM_TRIALS, NUM_MEMORY_CHECKS_DURING_TRAVERSAL);
		Map<String, SingleParserBenchmarkResult> resultsByName = parserBenchmark.performBenchmark(benchmarkXmlFile);
		writeBenchmarkResults(resultsByName, benchmarkXmlFile, benchmarkResultFile);
	}

	private static void writeBenchmarkResults(Map<String, SingleParserBenchmarkResult> resultsByName, Path benchmarkXmlFile, Path benchmarkResultFile) throws IOException {
		CsvLineCollector lineCollector = new CsvLineCollector();

		writeBenchmarkResultHeader(benchmarkXmlFile, lineCollector);
		writeBenchmarkResultTableHeader(lineCollector, resultsByName.keySet());
		writeBenchmarkResultTableLines(lineCollector, resultsByName.values());

		Files.write(benchmarkResultFile, lineCollector.getLines());
	}

	private static void writeBenchmarkResultHeader(Path benchmarkXmlFile, CsvLineCollector lineCollector) throws IOException {
		lineCollector.addLine("XML Benchmark Result");
		lineCollector.addLine("File:", benchmarkXmlFile.toString());
		lineCollector.addLine("Size:", DECIMAL_FORMAT.format(Files.size(benchmarkXmlFile) * BYTES_TO_MEGABYTES_FACTOR) + " MB");
		lineCollector.addLine("Trials:", String.valueOf(NUM_TRIALS));
		lineCollector.addLine();
	}

	private static void writeBenchmarkResultTableHeader(CsvLineCollector lineCollector, Collection<String> parserNames) {
		List<String> firstLine = new ArrayList<>(3 + parserNames.size());
		firstLine.add("Parsing Method");
		firstLine.add("");
		firstLine.add("");
		firstLine.addAll(parserNames);
		lineCollector.addLine(firstLine);
	}

	private static void writeBenchmarkResultTableLines(CsvLineCollector lineCollector, Collection<SingleParserBenchmarkResult> results) {
		BenchmarkResultWriter writer = new BenchmarkResultWriter(lineCollector, results);

		writer.writeResults("Document Parsing Time [s]",	"", SingleParserBenchmarkResult::getDocumentParseTimeMs,	MILLIS_TO_SECONDS_FACTOR);
		writer.writeResults("Tree Traversal Time [s]",		"", SingleParserBenchmarkResult::getTraversalTimeMs,		MILLIS_TO_SECONDS_FACTOR);
		writer.writeResults("Total Parsing Time [s]",		"", SingleParserBenchmarkResult::getTotalTimeMs,			MILLIS_TO_SECONDS_FACTOR);

		writer.writeResults("Memory Consumption without GC [MB]", "after parsing document", SingleParserBenchmarkResult::getDocumentMemoryConsumptionWithoutGc, BYTES_TO_MEGABYTES_FACTOR);
		for (int i = 1; i <= NUM_MEMORY_CHECKS_DURING_TRAVERSAL; i++) {
			int index = i - 1;
			writer.writeResults("", "after " + Math.round(100.0 * i/NUM_MEMORY_CHECKS_DURING_TRAVERSAL) + " % tree traversal", result -> result.getTraversalMemoryConsumptionsWithoutGc(index), BYTES_TO_MEGABYTES_FACTOR);
		}

		writer.writeResults("Memory Consumption with GC [MB]", "after parsing document", SingleParserBenchmarkResult::getDocumentMemoryConsumptionWithGc, BYTES_TO_MEGABYTES_FACTOR);
		for (int i = 1; i <= NUM_MEMORY_CHECKS_DURING_TRAVERSAL; i++) {
			int index = i - 1;
			writer.writeResults("", "after " + Math.round(100.0 * i/NUM_MEMORY_CHECKS_DURING_TRAVERSAL) + " % tree traversal", result -> result.getTraversalMemoryConsumptionsWithGc(index), BYTES_TO_MEGABYTES_FACTOR);
		}
	}

	private static class BenchmarkResultWriter
	{
		private final CsvLineCollector							lineCollector;
		private final Collection<SingleParserBenchmarkResult>	results;

		BenchmarkResultWriter(CsvLineCollector lineCollector, Collection<SingleParserBenchmarkResult> results) {
			this.lineCollector = lineCollector;
			this.results = results;
		}

		void writeResults(String col1, String col2, Function<SingleParserBenchmarkResult, LongValueStatistics> statisticsExtractor, double scaleFactor) {
			boolean firstLine = true;
			for (String statisticalValueExtractorName : STATISTICAL_VALUE_EXTRACTORS_BY_NAME.keySet()) {
				List<String> line = new ArrayList<>(3 + results.size());
				if (firstLine) {
					line.add(col1);
					line.add(col2);
				} else {
					line.add("");
					line.add("");
				}
				line.add(statisticalValueExtractorName);
				ToDoubleFunction<LongValueStatistics> statisticalValueExtractor = STATISTICAL_VALUE_EXTRACTORS_BY_NAME.get(statisticalValueExtractorName);
				for (SingleParserBenchmarkResult result : results) {
					LongValueStatistics statistics = statisticsExtractor.apply(result);
					double statisticalValue = statisticalValueExtractor.applyAsDouble(statistics);
					double value = statisticalValue * scaleFactor;
					line.add(DECIMAL_FORMAT.format(value));
				}
				lineCollector.addLine(line);
				firstLine = false;
			}
		}
	}

}
