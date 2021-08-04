package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Attr;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This object factory creates for each depth and type {@code k} instances of a node
 * with that depth and type (if so many are requested at all) and then starts to reuse
 * them in round-robin fashion. This ensures that you can hold references to {@code k} subsequent
 * siblings. However, note that since parsing is done stream-based, you can only access
 * data of those node that is stored in those node instances and does not have
 * to be parsed from the stream. This holds, e.g., for node names and attributes, but
 * not for child nodes.<br/>
 * <br/>
 * The reuse delay {@code k >= 1} is configurable.
 */
class ObjectFactoryDelayedReuse extends DefaultObjectFactory
{
	private final int																reusableDelay;
	private final List<RoundRobinCycle<ChildIterator>>								childIteratorCycles			= new ArrayList<>();
	private final List<RoundRobinCycle<ElementImpl>>								elementCycles				= new ArrayList<>();
	private final List<RoundRobinCycle<TextImpl>>									textCycles					= new ArrayList<>();
	private final List<RoundRobinCycle<CDATASectionImpl>>							cDataSectionCycles			= new ArrayList<>();
	private final List<RoundRobinCycle<CommentImpl>>								commentCycles				= new ArrayList<>();
	private final List<RoundRobinCycle<ProcessingInstructionImpl>>					processingInstructionCycles	= new ArrayList<>();
	private final List<RoundRobinCycle<Map<String, Attr>>>							attributesByQNameMapCycles	= new ArrayList<>();
	private final List<RoundRobinCycle<ReusableElementsCollection<NamespaceImpl>>>	namespaceCollections		= new ArrayList<>();
	private final List<RoundRobinCycle<ReusableElementsCollection<AttrImpl>>>		attributeCollections		= new ArrayList<>();

	/**
	 * @param streamReader The XML stream reader
	 * @param nodeFactory The factory used by most nodes to create further nodes
	 * @param reuseDelay The number of instances of each node type and depth to create
	 *                   of such a node until the first one is reused
	 */
	ObjectFactoryDelayedReuse(ExtendedXmlStreamReader streamReader, NodeFactory nodeFactory, int reuseDelay) {
		super(streamReader, nodeFactory);
		this.reusableDelay = reuseDelay;
	}

	@Override
	public ChildIterator createChildIterator(int depth) {
		ChildIterator childIterator = getAndNext(childIteratorCycles, depth);
		return childIterator != null ? childIterator : setAndNext(childIteratorCycles, depth, super.createChildIterator(depth));
	}

	@Override
	public ElementImpl createElement(int depth) {
		ElementImpl element = getAndNext(elementCycles, depth);
		return element != null ? element : setAndNext(elementCycles, depth, super.createElement(depth));
	}

	@Override
	public TextImpl createText(int depth) {
		TextImpl text = getAndNext(textCycles, depth);
		return text != null ? text : setAndNext(textCycles, depth, super.createText(depth));
	}

	@Override
	public CDATASectionImpl createCDataSection(int depth) {
		CDATASectionImpl cDataSection = getAndNext(cDataSectionCycles, depth);
		return cDataSection != null ? cDataSection : setAndNext(cDataSectionCycles, depth, super.createCDataSection(depth));
	}

	@Override
	public CommentImpl createComment(int depth) {
		CommentImpl comment = getAndNext(commentCycles, depth);
		return comment != null ? comment : setAndNext(commentCycles, depth, super.createComment(depth));
	}

	@Override
	public ProcessingInstructionImpl createProcessingInstruction(int depth) {
		ProcessingInstructionImpl processingInstruction = getAndNext(processingInstructionCycles, depth);
		return processingInstruction != null ? processingInstruction : setAndNext(processingInstructionCycles, depth, super.createProcessingInstruction(depth));
	}

	@Override
	public Map<String, Attr> createAttributesByQNameMap(int depth) {
		makeNamespacesAndElementsReusable(depth);
		Map<String, Attr> attributesByQName = getAndNext(attributesByQNameMapCycles, depth);
		if (attributesByQName != null) {
			attributesByQName.clear();
			return attributesByQName;
		}
		return setAndNext(attributesByQNameMapCycles, depth, super.createAttributesByQNameMap(depth));
	}

	/**
	 * Namespaces and attributes cannot be reused immediately because an element can have multiple of them
	 * at the same time. Since namespaces and attributes are stored in an attributesByQNameMap,
	 * we ensure that an instance is not reused multiple times between two consecutive calls of
	 * {@link #createAttributesByQNameMap(int)}.
	 */
	private void makeNamespacesAndElementsReusable(int depth) {
		RoundRobinCycle<ReusableElementsCollection<NamespaceImpl>> namespaceCycle = ImplUtils.get(namespaceCollections, depth);
		if (namespaceCycle == null) {
			namespaceCycle = ImplUtils.set(namespaceCollections, depth, new RoundRobinCycle<>(reusableDelay));
		}
		namespaceCycle.next();
		ReusableElementsCollection<NamespaceImpl> namespaces = namespaceCycle.get();
		if (namespaces == null) {
			namespaces = namespaceCycle.set(new ReusableElementsCollection<>());
		}
		namespaces.makeElementsReusable();

		RoundRobinCycle<ReusableElementsCollection<AttrImpl>> attributeCycle = ImplUtils.get(attributeCollections, depth);
		if (attributeCycle == null) {
			attributeCycle = ImplUtils.set(attributeCollections, depth, new RoundRobinCycle<>(reusableDelay));
		}
		attributeCycle.next();
		ReusableElementsCollection<AttrImpl> attributes = attributeCycle.get();
		if (attributes == null) {
			attributes = attributeCycle.set(new ReusableElementsCollection<>());
		}
		attributes.makeElementsReusable();
	}

	@Override
	public NamespaceImpl createNamespace(int depth) {
		RoundRobinCycle<ReusableElementsCollection<NamespaceImpl>> namespaceCycle = namespaceCollections.get(depth);
		ReusableElementsCollection<NamespaceImpl> namespaces = namespaceCycle.get();
		NamespaceImpl namespace = namespaces.pollReusableElement();
		return namespace != null ? namespace : namespaces.addElementForLaterReuse(super.createNamespace(depth));
	}

	@Override
	public AttrImpl createAttribute(int depth) {
		RoundRobinCycle<ReusableElementsCollection<AttrImpl>> attributeCycle = attributeCollections.get(depth);
		ReusableElementsCollection<AttrImpl> attributes = attributeCycle.get();
		AttrImpl attribute = attributes.pollReusableElement();
		return attribute != null ? attribute : attributes.addElementForLaterReuse(super.createAttribute(depth));
	}

	private <T> T getAndNext(List<RoundRobinCycle<T>> roundRobinCycles, int index) {
		RoundRobinCycle<T> roundRobinCycle = ImplUtils.get(roundRobinCycles, index);
		return roundRobinCycle != null ? roundRobinCycle.getAndNext() : null;
	}

	private <T> T setAndNext(List<RoundRobinCycle<T>> roundRobinCycles, int index, T element) {
		RoundRobinCycle<T> roundRobinCycle = null;
		if (index < roundRobinCycles.size()) {
			roundRobinCycle = roundRobinCycles.get(index);
		}
		if (roundRobinCycle == null) {
			roundRobinCycle = new RoundRobinCycle<>(reusableDelay);
			ImplUtils.set(roundRobinCycles, index, roundRobinCycle);
		}
		return roundRobinCycle.setAndNext(element);
	}

	private static class RoundRobinCycle<T>
	{
		private final List<T>	data;
		private int				index;

		RoundRobinCycle(int size) {
			data = new ArrayList<>(size);
			for (int i = 0; i < size; i++) {
				data.add(null);
			}
		}

		T get() {
			return data.get(index);
		}

		T getAndNext() {
			T element = data.get(index);
			if (element != null) {
				/*
				 * Only rotate if an element is available. Otherwise, setNext() will be called, setting an
				 * element to the current position and moving next afterwards.
				 */
				next();
			}
			return element;
		}

		T set(T element) {
			data.set(index, element);
			return element;
		}

		T setAndNext(T element) {
			data.set(index, element);
			next();
			return element;
		}

		void next() {
			index = index == data.size() - 1 ? 0 : index + 1;
		}
	}
}
