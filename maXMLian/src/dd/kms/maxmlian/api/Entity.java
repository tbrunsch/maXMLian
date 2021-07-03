package dd.kms.maxmlian.api;

public interface Entity extends Node
{
	@Override
	default NodeType getNodeType() {
		return NodeType.ENTITY;
	}

	String getPublicId();
	String getSystemId();
	String getNotationName();
}
