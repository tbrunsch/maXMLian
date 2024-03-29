package dd.kms.maxmlian.api;

import java.util.Map;

public interface DocumentType extends Node
{
	@Override
	default NodeType getNodeType() {
		return NodeType.DOCUMENT_TYPE;
	}

	/**
	 * Returns the name of the document type definition.
	 * @throws UnsupportedOperationException	if the internally used StAX parser does
	 *  not provide a way to retrieve this information.
	 */
	String getName();
	Map<String, Entity> getEntities();
	Map<String, Notation> getNotations();

	/**
	 * Returns the public id of the document type definition.
	 * @throws UnsupportedOperationException	if the internally used StAX parser does
	 *  not provide a way to retrieve this information.
	 */
	String getPublicId();

	/**
	 * Returns the system id of the document type definition.
	 * @throws UnsupportedOperationException	if the internally used StAX parser does
	 *  not provide a way to retrieve this information.
	 */
	String getSystemId();
	String getInternalSubset();
}
