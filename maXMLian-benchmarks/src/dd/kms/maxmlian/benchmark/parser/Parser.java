package dd.kms.maxmlian.benchmark.parser;

import dd.kms.maxmlian.benchmark.monitor.Monitor;

import java.nio.file.Path;

public interface Parser
{
	void setMonitor(Monitor monitor);
	void parseXml(Path xmlFile) throws Exception;
}
