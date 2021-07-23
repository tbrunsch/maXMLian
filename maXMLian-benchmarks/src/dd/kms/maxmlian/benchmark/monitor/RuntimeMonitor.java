package dd.kms.maxmlian.benchmark.monitor;

public class RuntimeMonitor extends AbstractMonitor
{
	private final long	totalTraversalProgress;

	private long		testStartTime;
	private long		traversalStartTime;
	private long		traversalEndTime;

	public RuntimeMonitor(long totalTraversalProgress) {
		this.totalTraversalProgress = totalTraversalProgress;
	}

	public long getDocumentParseTimeMs() {
		return traversalStartTime - testStartTime;
	}

	public long getTraversalTimeMs() {
		return traversalEndTime - traversalStartTime;
	}

	@Override
	public void testStarted() {
		testStartTime = System.currentTimeMillis();
	}

	@Override
	public void documentCreationFinished() {
		traversalStartTime = System.currentTimeMillis();
	}

	@Override
	public void traversalProgress() {
		super.traversalProgress();
		if (getTraversalProgress() == totalTraversalProgress) {
			traversalEndTime = System.currentTimeMillis();
		}
	}
}
