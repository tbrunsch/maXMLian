package dd.kms.maxmlian.api;

public interface Comment extends CharacterData
{
	@Override
	default NodeType getNodeType() {
		return NodeType.COMMENT;
	}
}
