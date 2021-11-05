package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Attr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dd.kms.maxmlian.impl.ImplUtils.get;
import static dd.kms.maxmlian.impl.ImplUtils.set;

/**
 * This object factory reuses created instances as fast as possible. Whenever a node
 * of a certain depth and a certain type is requested, the previous node instance
 * of the same depth and type are returned. Only the first node with that depth
 * and type will be instantiated.
 */
class ObjectFactoryWithReuse extends DefaultObjectFactory
{
	private final List<ChildIterator>								childIterators			= new ArrayList<>();
	private final List<ElementImpl>									elements				= new ArrayList<>();
	private final List<TextImpl>									texts					= new ArrayList<>();
	private final List<CDATASectionImpl>							cDataSections			= new ArrayList<>();
	private final List<CommentImpl>									comments				= new ArrayList<>();
	private final List<ProcessingInstructionImpl>					processingInstructions	= new ArrayList<>();
	private final List<Map<String, Attr>>							attributesByQNameMaps	= new ArrayList<>();
	private final List<ReusableElementsCollection<NamespaceImpl>>	namespaceCollections	= new ArrayList<>();
	private final List<ReusableElementsCollection<AttrImpl>>		attributeCollections	= new ArrayList<>();

	ObjectFactoryWithReuse(ExtendedXmlStreamReader streamReader, NodeFactory nodeFactory) {
		super(streamReader, nodeFactory);
	}

	@Override
	public ChildIterator createChildIterator(int depth) {
		ChildIterator childIterator = get(childIterators, depth);
		return childIterator != null ? childIterator : set(childIterators, depth, super.createChildIterator(depth));
	}

	@Override
	public ElementImpl createElement(int depth) {
		ElementImpl element = get(elements, depth);
		return element != null ? element : set(elements, depth, super.createElement(depth));
	}

	@Override
	public TextImpl createText(int depth) {
		TextImpl text = get(texts, depth);
		return text != null ? text : set(texts, depth, super.createText(depth));
	}

	@Override
	public CDATASectionImpl createCDataSection(int depth) {
		CDATASectionImpl cDataSection = get(cDataSections, depth);
		return cDataSection != null ? cDataSection : set(cDataSections, depth, super.createCDataSection(depth));
	}

	@Override
	public CommentImpl createComment(int depth) {
		CommentImpl comment = get(comments, depth);
		return comment != null ? comment : set(comments, depth, super.createComment(depth));
	}

	@Override
	public ProcessingInstructionImpl createProcessingInstruction(int depth) {
		ProcessingInstructionImpl processingInstruction = get(processingInstructions, depth);
		return processingInstruction != null ? processingInstruction : set(processingInstructions, depth, super.createProcessingInstruction(depth));
	}

	@Override
	public Map<String, Attr> createAttributesByQNameMap(int depth) {
		makeNamespacesAndElementsReusable(depth);
		Map<String, Attr> attributesByQNameMap = get(attributesByQNameMaps, depth);
		if (attributesByQNameMap != null) {
			attributesByQNameMap.clear();
			return attributesByQNameMap;
		}
		return set(attributesByQNameMaps, depth, super.createAttributesByQNameMap(depth));
	}

	/**
	 * Namespaces and attributes cannot be reused immediately because an element can have multiple of them
	 * at the same time. Since namespaces and attributes are stored in an attributesByQNameMap,
	 * we ensure that an instance is not reused multiple times between two consecutive calls of
	 * {@link #createAttributesByQNameMap(int)}.
	 */
	private void makeNamespacesAndElementsReusable(int depth) {
		while (depth >= namespaceCollections.size()) {
			namespaceCollections.add(new ReusableElementsCollection<>());
		}
		namespaceCollections.get(depth).makeElementsReusable();
		while (depth >= attributeCollections.size()) {
			attributeCollections.add(new ReusableElementsCollection<>());
		}
		attributeCollections.get(depth).makeElementsReusable();
	}

	@Override
	public NamespaceImpl createNamespace(int depth) {
		ReusableElementsCollection<NamespaceImpl> namespaces = namespaceCollections.get(depth);
		NamespaceImpl namespace = namespaces.pollReusableElement();
		return namespace != null ? namespace : namespaces.addElementForLaterReuse(super.createNamespace(depth));
	}

	@Override
	public AttrImpl createAttribute(int depth) {
		ReusableElementsCollection<AttrImpl> attributes = attributeCollections.get(depth);
		AttrImpl attribute = attributes.pollReusableElement();
		return attribute != null ? attribute : attributes.addElementForLaterReuse(super.createAttribute(depth));
	}

}
