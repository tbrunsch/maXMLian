package dd.kms.maxmlian.api;

import java.util.Map;

public interface DocumentType extends Node
{
	@Override
	default NodeType getNodeType() {
		return NodeType.DOCUMENT_TYPE;
	}

	String getName();
	Map<String, Entity> getEntities();
	Map<String, Notation> getNotations();
	String getPublicId();
	String getSystemId();
	String getInternalSubset();
}
