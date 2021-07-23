package dd.kms.maxmlian.benchmark.monitor;

abstract class AbstractMonitor implements Monitor
{
	private long	traversalProgress;

	@Override
	public void traversalProgress() {
		traversalProgress++;
	}

	public long getTraversalProgress() {
		return traversalProgress;
	}
}
