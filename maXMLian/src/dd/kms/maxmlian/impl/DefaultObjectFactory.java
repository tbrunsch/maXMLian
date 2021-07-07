package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Attr;

import javax.xml.stream.events.Characters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This object factory creates new instances whenever an instance of a certain
 * type is requested. It does not reuse instances it has created earlier.
 */
class DefaultObjectFactory implements ObjectFactory
{
	private final ExtendedXmlEventReader	eventReader;
	private final NodeFactory				nodeFactory;

	DefaultObjectFactory(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
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
	public Map<String, Attr> createAttributesByQNameMap(int depth) {
		return new LinkedHashMap<>(4, 0.75f);
	}

	@Override
	public NamespaceImpl createNamespace(int depth) {
		return new NamespaceImpl(nodeFactory);
	}

	@Override
	public AttrImpl createAttribute(int depth) {
		return new AttrImpl(nodeFactory);
	}
}
