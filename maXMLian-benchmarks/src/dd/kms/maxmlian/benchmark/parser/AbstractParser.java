package dd.kms.maxmlian.benchmark.parser;

import dd.kms.maxmlian.benchmark.monitor.Monitor;

import java.nio.file.Path;

abstract class AbstractParser implements Parser
{
	private Monitor	monitor;

	abstract void doParseXml(Path xmlFile) throws Exception;

	void documentCreationFinished() {
		if (monitor != null) {
			monitor.documentCreationFinished();
		}
	}

	void traversalProgress() {
		if (monitor != null) {
			monitor.traversalProgress();
		}
	}

	@Override
	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public void parseXml(Path xmlFile) throws Exception {
		if (monitor != null) {
			monitor.testStarted();
		}
		doParseXml(xmlFile);
	}
}
