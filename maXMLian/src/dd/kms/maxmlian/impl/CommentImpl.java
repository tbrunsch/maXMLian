package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Comment;
import dd.kms.maxmlian.api.NodeType;

class CommentImpl extends CharacterDataImpl implements Comment
{
	CommentImpl(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initializeFromComment(javax.xml.stream.events.Comment comment) {
		super.initializePosition();
		setData(comment.getText());
	}

	@Override
	public String getNodeName() {
		return "#comment";
	}
}
