package dd.kms.maxmlian.api;

public interface Attr extends Node
{
	@Override
	default NodeType getNodeType() {
		return NodeType.ATTRIBUTE;
	}

	String getName();
	String getValue();
	boolean isId();
}
