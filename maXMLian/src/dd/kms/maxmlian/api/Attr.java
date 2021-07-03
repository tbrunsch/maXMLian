package dd.kms.maxmlian.api;

public interface Attr extends Node
{
	@Override
	default NodeType getNodeType() {
		return NodeType.ATTRIBUTE;
	}

	String getName();
	boolean getSpecified();
	String getValue();
	boolean isId();
}
