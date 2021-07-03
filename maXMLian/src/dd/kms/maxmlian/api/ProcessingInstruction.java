package dd.kms.maxmlian.api;

public interface ProcessingInstruction extends Node
{
	@Override
	default NodeType getNodeType() {
		return NodeType.PROCESSING_INSTRUCTION;
	}

	String getTarget();
	String getData();
}
