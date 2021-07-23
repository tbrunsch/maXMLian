package dd.kms.maxmlian.benchmark;

class SingleParserBenchmarkResult
{
	private final LongValueStatistics	documentParseTimeMs						= new LongValueStatistics();
	private final LongValueStatistics	traversalTimeMs							= new LongValueStatistics();
	private final LongValueStatistics	totalTimeMs								= new LongValueStatistics();
	private final LongValueStatistics	documentMemoryConsumptionWithoutGc		= new LongValueStatistics();
	private final LongValueStatistics[]	traversalMemoryConsumptionsWithoutGc;
	private final LongValueStatistics	documentMemoryConsumptionWithGc			= new LongValueStatistics();
	private final LongValueStatistics[]	traversalMemoryConsumptionsWithGc;

	SingleParserBenchmarkResult(int numMemoryChecksDuringTraversal) {
		this.traversalMemoryConsumptionsWithoutGc = createLongValueStatistics(numMemoryChecksDuringTraversal);
		this.traversalMemoryConsumptionsWithGc = createLongValueStatistics(numMemoryChecksDuringTraversal);
	}

	void addDocumentParseTimeMs(long documentParseTimeMs) {
		this.documentParseTimeMs.addValue(documentParseTimeMs);
	}

	void addTraversalTimeMs(long traversalTimeMs) {
		this.traversalTimeMs.addValue(traversalTimeMs);
	}

	void addTotalTimeMs(long totalTimeMs) {
		this.totalTimeMs.addValue(totalTimeMs);
	}

	void addDocumentMemoryConsumptionWithoutGc(long documentMemoryConsumptionWithoutGc) {
		this.documentMemoryConsumptionWithoutGc.addValue(documentMemoryConsumptionWithoutGc);
	}

	void addTraversalMemoryConsumptionWithoutGc(int index, long traversalMemoryConsumptionWithoutGc) {
		this.traversalMemoryConsumptionsWithoutGc[index].addValue(traversalMemoryConsumptionWithoutGc);
	}

	void addDocumentMemoryConsumptionWithGc(long documentMemoryConsumptionWithGc) {
		this.documentMemoryConsumptionWithGc.addValue(documentMemoryConsumptionWithGc);
	}

	void addTraversalMemoryConsumptionWithGc(int index, long traversalMemoryConsumptionWithGc) {
		this.traversalMemoryConsumptionsWithGc[index].addValue(traversalMemoryConsumptionWithGc);
	}

	LongValueStatistics getDocumentParseTimeMs() {
		return documentParseTimeMs;
	}

	LongValueStatistics getTraversalTimeMs() {
		return traversalTimeMs;
	}

	LongValueStatistics getTotalTimeMs() {
		return totalTimeMs;
	}

	LongValueStatistics getDocumentMemoryConsumptionWithoutGc() {
		return documentMemoryConsumptionWithoutGc;
	}

	LongValueStatistics getDocumentMemoryConsumptionWithGc() {
		return documentMemoryConsumptionWithGc;
	}

	LongValueStatistics getTraversalMemoryConsumptionsWithoutGc(int index) {
		return traversalMemoryConsumptionsWithoutGc[index];
	}

	LongValueStatistics getTraversalMemoryConsumptionsWithGc(int index) {
		return traversalMemoryConsumptionsWithGc[index];
	}

	private static LongValueStatistics[] createLongValueStatistics(int length) {
		LongValueStatistics[] longValueStatistics = new LongValueStatistics[length];
		for (int i = 0; i < length; i++) {
			longValueStatistics[i] = new LongValueStatistics();
		}
		return longValueStatistics;
	}
}
