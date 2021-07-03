package dd.kms.maxmlian.api;

public interface CDATASection extends Text
{
	@Override
	default NodeType getNodeType() {
		return NodeType.CDATA_SECTION;
	}
}
