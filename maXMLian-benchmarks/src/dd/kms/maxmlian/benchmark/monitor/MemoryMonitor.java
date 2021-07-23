package dd.kms.maxmlian.benchmark.monitor;

import dd.kms.maxmlian.benchmark.BenchmarkUtils;

public class MemoryMonitor extends AbstractMonitor
{

	private final long		totalTraversalProgress;
	private final boolean	withGarbageCollection;

	private long			initiallyUsedMemory;
	private long			documentMemoryConsumption;
	private long[]			traversalMemoryConsumptions;

	private int				traversalMemoryConsumptionIndex;
	private long			nextTraversalMemoryConsumptionTraversalProgress;

	public MemoryMonitor(long totalTraversalProgress, boolean withGarbageCollection, int numMemoryChecksDuringTraversal) {
		this.totalTraversalProgress = totalTraversalProgress;
		this.withGarbageCollection = withGarbageCollection;
		this.traversalMemoryConsumptions = new long[numMemoryChecksDuringTraversal];
	}

	public long getDocumentMemoryConsumption() {
		return documentMemoryConsumption;
	}

	public long[] getTraversalMemoryConsumptions() {
		return traversalMemoryConsumptions;
	}

	@Override
	public void testStarted() {
		BenchmarkUtils.forceGarbageCollection();
		initiallyUsedMemory = BenchmarkUtils.getUsedMemory();
		traversalMemoryConsumptionIndex = 0;
		nextTraversalMemoryConsumptionTraversalProgress = getTraversalMemoryConsumptionTraversalProgress(traversalMemoryConsumptionIndex);
	}

	@Override
	public void documentCreationFinished() {
		collectGarbageIfRequested();
		documentMemoryConsumption = Math.max(BenchmarkUtils.getUsedMemory() - initiallyUsedMemory, 0);
	}

	@Override
	public void traversalProgress() {
		super.traversalProgress();
		if (getTraversalProgress() != nextTraversalMemoryConsumptionTraversalProgress) {
			return;
		}
		collectGarbageIfRequested();
		traversalMemoryConsumptions[traversalMemoryConsumptionIndex] = Math.max(BenchmarkUtils.getUsedMemory() - initiallyUsedMemory, 0);
		traversalMemoryConsumptionIndex++;
		nextTraversalMemoryConsumptionTraversalProgress = getTraversalMemoryConsumptionTraversalProgress(traversalMemoryConsumptionIndex);
	}

	private void collectGarbageIfRequested() {
		if (!withGarbageCollection) {
			return;
		}
		BenchmarkUtils.forceGarbageCollection();
	}

	private long getTraversalMemoryConsumptionTraversalProgress(int index) {
		long progress = Math.round((index + 1) / ((double) traversalMemoryConsumptions.length) * totalTraversalProgress);
		return progress;
	}
}
