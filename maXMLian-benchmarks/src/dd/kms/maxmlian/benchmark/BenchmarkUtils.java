package dd.kms.maxmlian.benchmark;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class BenchmarkUtils
{
	private static final int	NUMBER_OF_CG_RUNS	= 3;

	public static void forceGarbageCollection() {
		for (int i = 0; i < NUMBER_OF_CG_RUNS; i++) {
			System.gc();
		}
	}

	public static long getUsedMemory() {
		MemoryMXBean memBean = ManagementFactory.getMemoryMXBean() ;
		MemoryUsage heapMemoryUsage = memBean.getHeapMemoryUsage();
		return heapMemoryUsage.getUsed();
	}
}
