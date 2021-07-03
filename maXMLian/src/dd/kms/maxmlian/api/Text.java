package dd.kms.maxmlian.api;

public interface Text extends CharacterData
{
	@Override
	default NodeType getNodeType() {
		return NodeType.TEXT;
	}

	boolean isElementContentWhitespace();
}
