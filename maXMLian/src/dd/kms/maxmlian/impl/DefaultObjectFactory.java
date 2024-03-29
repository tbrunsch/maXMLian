package dd.kms.maxmlian.impl;

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
	public NamedAttributeMapImpl createNamedAttributeMap(int depth) {
		return new NamedAttributeMapImpl();
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
