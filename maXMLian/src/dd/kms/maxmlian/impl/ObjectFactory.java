package dd.kms.maxmlian.impl;

import javax.xml.stream.events.Characters;
import java.util.List;

/**
 * Interface for all strategies that provide instances of certain types for a given depth.
 */
interface ObjectFactory
{
	ChildIterator createChildIterator(int depth);
	ElementImpl createElement(int depth);
	TextImpl createText(int depth);
	CDATASectionImpl createCDataSection(int depth);
	CommentImpl createComment(int depth);
	ProcessingInstructionImpl createProcessingInstruction(int depth);
	NamespaceImpl createNamespace(int depth);
	AttrImpl createAttribute(int depth);

	List<Characters> createCharactersList(int depth);
}
