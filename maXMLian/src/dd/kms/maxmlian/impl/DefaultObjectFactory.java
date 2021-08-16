package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Attr;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This object factory creates new instances whenever an instance of a certain
 * type is requested. It does not reuse instances it has created earlier.
 */
class DefaultObjectFactory implements ObjectFactory
{
	private final ExtendedXmlStreamReader	streamReader;
	private final NodeFactory				nodeFactory;

	DefaultObjectFactory(ExtendedXmlStreamReader streamReader, NodeFactory nodeFactory) {
		this.streamReader = streamReader;
		this.nodeFactory = nodeFactory;
	}

	@Override
	public ChildIterator createChildIterator(int depth) {
		return new ChildIterator(streamReader, nodeFactory);
	}

	@Override
	public ElementImpl createElement(int depth) {
		return new ElementImpl(streamReader, nodeFactory);
	}

	@Override
	public TextImpl createText(int depth) {
		return new TextImpl(streamReader, nodeFactory);
	}

	@Override
	public CDATASectionImpl createCDataSection(int depth) {
		return new CDATASectionImpl(streamReader, nodeFactory);
	}

	@Override
	public CommentImpl createComment(int depth) {
		return new CommentImpl(streamReader, nodeFactory);
	}

	@Override
	public ProcessingInstructionImpl createProcessingInstruction(int depth) {
		return new ProcessingInstructionImpl(streamReader, nodeFactory);
	}

	@Override
	public Map<String, Attr> createAttributesByQNameMap(int depth) {
		return new LinkedHashMap<>(4, 0.75f);
	}

	@Override
	public NamespaceImpl createNamespace(int depth) {
		return new NamespaceImpl(nodeFactory.isNamespaceAware());
	}

	@Override
	public AttrImpl createAttribute(int depth) {
		return new AttrImpl(nodeFactory.isNamespaceAware());
	}
}
