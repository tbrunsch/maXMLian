package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Attr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This object factory creates reuses created instances as much as possible.
 */
class ObjectFactoryImmediateReuse extends DefaultObjectFactory
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

	ObjectFactoryImmediateReuse(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
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
	 * we ensure that an element is not reused multiple times between two consecutive calls of
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

	private static <T> T get(List<T> list, int index) {
		return index < list.size() ? list.get(index) : null;
	}

	private static <T> T set(List<T> list, int index, T value) {
		if (index < list.size()) {
			list.set(index, value);
		} else {
			while (list.size() < index) {
				list.add(null);
			}
			list.add(value);
		}
		return value;
	}

	private static class ReusableElementsCollection<T>
	{
		private final List<T>	data					= new ArrayList<>();
		private int				numNonReusableElements;

		T pollReusableElement() {
			if (numNonReusableElements == data.size()) {
				return null;
			}
			return data.get(numNonReusableElements++);
		}

		T addElementForLaterReuse(T element) {
			assert numNonReusableElements == data.size() : "This method is meant to be called if no reusable element is available anymore";
			data.add(element);
			numNonReusableElements++;
			return element;
		}

		void makeElementsReusable() {
			numNonReusableElements = 0;
		}
	}
}
