package dd.kms.maxmlian.api;

public enum NodeType
{
	/**
	 * The node is of type {@link Element}.
	 */
	ELEMENT,

	/**
	 * The node is of type {@link Attr}. Attribute nodes are not part of the
	 * DOM tree, but {@link Element#getAttributes()} returns a map whose
	 * values are attribute nodes.
	 */
	ATTRIBUTE,

	/**
	 * The node is of type {@link Text} (but not of type {@link CDATASection}.
	 */
	TEXT,

	/**
	 * The node is of type {@link CDATASection}.
	 */
	CDATA_SECTION,

	/**
	 * The node is of type {@link Entity}. Entity nodes are not part of the
	 * DOM tree, but {@link DocumentType#getEntities()} returns a map whose
	 * values are entity nodes.
	 */
	ENTITY,

	/**
	 * This node is of type {@link ProcessingInstruction}.
	 */
	PROCESSING_INSTRUCTION,

	/**
	 * This node is of type {@link Comment}.
	 */
	COMMENT,

	/**
	 * This node is of type {@link Document}.
	 */
	DOCUMENT,

	/**
	 * This node is of type {@link DocumentType}.
	 */
	DOCUMENT_TYPE,

	/**
	 * This node is of type {@link Notation}. Notation nodes are not part of the
	 * DOM tree, but {@link DocumentType#getNotations()} returns a map whose
	 * values are notation nodes.
	 */
	NOTATION
}
