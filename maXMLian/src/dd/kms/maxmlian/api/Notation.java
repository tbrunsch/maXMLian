package dd.kms.maxmlian.api;

public interface Notation extends Node
{
	@Override
	default NodeType getNodeType() {
		return NodeType.NOTATION;
	}

	String getPublicId();
	String getSystemId();
}
