package dd.kms.maxmlian.benchmark;

class LongValueStatistics
{
	private long	minValue	= Long.MAX_VALUE;
	private long	maxValue	= Long.MIN_VALUE;
	private long	sumValues;
	private int		valuesCount;

	void addValue(long value) {
		minValue = Math.min(minValue, value);
		maxValue = Math.max(maxValue, value);
		sumValues += value;
		valuesCount++;
	}

	long getMinValue() {
		return minValue;
	}

	long getMaxValue() {
		return maxValue;
	}

	double getAverageValue() {
		return (double) sumValues / valuesCount;
	}
}
