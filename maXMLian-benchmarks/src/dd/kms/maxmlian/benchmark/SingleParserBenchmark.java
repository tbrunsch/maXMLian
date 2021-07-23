package dd.kms.maxmlian.benchmark;

import dd.kms.maxmlian.benchmark.monitor.TotalTraversalProgressDeterminationMonitor;
import dd.kms.maxmlian.benchmark.monitor.MemoryMonitor;
import dd.kms.maxmlian.benchmark.monitor.RuntimeMonitor;
import dd.kms.maxmlian.benchmark.parser.Parser;

import java.nio.file.Path;

class SingleParserBenchmark
{
	private final Path	xmlFile;
	private final int	numTrials;
	private final int	numMemoryChecksDuringTraversal;

	SingleParserBenchmark(Path xmlFile, int numTrials, int numMemoryChecksDuringTraversal) {
		if (numTrials <= 0) {
			throw new IllegalArgumentException("The number of repetitions must be positive");
		}
		if (numMemoryChecksDuringTraversal <= 0) {
			throw new IllegalArgumentException("At least one memory check during traversal is required");
		}
		this.xmlFile = xmlFile;
		this.numTrials = numTrials;
		this.numMemoryChecksDuringTraversal = numMemoryChecksDuringTraversal;
	}

	SingleParserBenchmarkResult performBenchmark(Parser parser) throws Exception {
		SingleParserBenchmarkResult	result = new SingleParserBenchmarkResult(numMemoryChecksDuringTraversal);

		BenchmarkUtils.forceGarbageCollection();
		long totalTraversalProgress = determineTotalTraversalProgress(parser);

		for (int trial = 0; trial < numTrials; trial++) {
			BenchmarkUtils.forceGarbageCollection();

			System.out.println("  Trial " + (trial + 1) + " of " + numTrials + "...");
			RuntimeMonitor runtimeMonitor = new RuntimeMonitor(totalTraversalProgress);
			parser.setMonitor(runtimeMonitor);
			parser.parseXml(xmlFile);
			long documentParseTimeMs = runtimeMonitor.getDocumentParseTimeMs();
			result.addDocumentParseTimeMs(documentParseTimeMs);
			long traversalTimeMs = runtimeMonitor.getTraversalTimeMs();
			result.addTraversalTimeMs(traversalTimeMs);
			result.addTotalTimeMs(documentParseTimeMs + traversalTimeMs);;

			BenchmarkUtils.forceGarbageCollection();

			MemoryMonitor memoryMonitorWithoutGc = new MemoryMonitor(totalTraversalProgress, false, numMemoryChecksDuringTraversal);
			parser.setMonitor(memoryMonitorWithoutGc);
			parser.parseXml(xmlFile);
			result.addDocumentMemoryConsumptionWithoutGc(memoryMonitorWithoutGc.getDocumentMemoryConsumption());
			long[] traversalMemoryConsumptionsWithoutGc = memoryMonitorWithoutGc.getTraversalMemoryConsumptions();
			for (int k = 0; k < numMemoryChecksDuringTraversal; k++) {
				result.addTraversalMemoryConsumptionWithoutGc(k, traversalMemoryConsumptionsWithoutGc[k]);
			}

			BenchmarkUtils.forceGarbageCollection();

			MemoryMonitor memoryMonitorWithGc = new MemoryMonitor(totalTraversalProgress, true, numMemoryChecksDuringTraversal);
			parser.setMonitor(memoryMonitorWithGc);
			parser.parseXml(xmlFile);
			result.addDocumentMemoryConsumptionWithGc(memoryMonitorWithGc.getDocumentMemoryConsumption());
			long[] traversalMemoryConsumptionsWithGc = memoryMonitorWithGc.getTraversalMemoryConsumptions();
			for (int k = 0; k < numMemoryChecksDuringTraversal; k++) {
				result.addTraversalMemoryConsumptionWithGc(k, traversalMemoryConsumptionsWithGc[k]);
			}
		}
		return result;
	}

	private long determineTotalTraversalProgress(Parser parser) throws Exception {
		TotalTraversalProgressDeterminationMonitor monitor = new TotalTraversalProgressDeterminationMonitor();
		parser.setMonitor(monitor);
		parser.parseXml(xmlFile);
		return monitor.getTraversalProgress();
	}
}
