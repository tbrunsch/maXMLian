package dd.kms.maxmlian.impl;

import javax.xml.stream.events.Characters;
import java.util.ArrayList;
import java.util.List;

/**
 * This object factory creates new instances whenever an instance of a certain
 * type is requested. It does not reuse instances it has created earlier.
 */
class ObjectFactoryWithoutReuse implements ObjectFactory
{
	private final ExtendedXmlEventReader	eventReader;
	private final NodeFactory				nodeFactory;

	ObjectFactoryWithoutReuse(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		this.eventReader = eventReader;
		this.nodeFactory = nodeFactory;
	}

	@Override
	public ChildIterator createChildIterator(int depth) {
		return new ChildIterator(eventReader, nodeFactory);
	}

	@Override
	public ElementImpl createElement(int depth) {
		return new ElementImpl(eventReader, nodeFactory);
	}

	@Override
	public TextImpl createText(int depth) {
		return new TextImpl(eventReader, nodeFactory);
	}

	@Override
	public CDATASectionImpl createCDataSection(int depth) {
		return new CDATASectionImpl(eventReader, nodeFactory);
	}

	@Override
	public CommentImpl createComment(int depth) {
		return new CommentImpl(eventReader, nodeFactory);
	}

	@Override
	public ProcessingInstructionImpl createProcessingInstruction(int depth) {
		return new ProcessingInstructionImpl(eventReader, nodeFactory);
	}

	@Override
	public NamespaceImpl createNamespace(int depth) {
		return new NamespaceImpl(nodeFactory);
	}

	@Override
	public AttrImpl createAttribute(int depth) {
		return new AttrImpl(nodeFactory);
	}

	@Override
	public List<Characters> createCharactersList(int depth) {
		return new ArrayList<>(1);
	}
}
