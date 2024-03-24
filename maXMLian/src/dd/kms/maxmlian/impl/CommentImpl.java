package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Comment;

class CommentImpl extends CharacterDataImpl implements Comment
{
	CommentImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	@Override
	public String getNodeName() {
		return "#comment";
	}
}
