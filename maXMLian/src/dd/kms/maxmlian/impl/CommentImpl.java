package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Comment;

class CommentImpl extends CharacterDataImpl implements Comment
{
	CommentImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initialize(String text) {
		super.initialize();
		setData(text);
	}

	@Override
	public String getNodeName() {
		return "#comment";
	}

	@Override
	public String getTextContent() {
		return getNodeValue();
	}

	@Override
	void appendTextContentTo(StringBuilder builder) {
		builder.append(getNodeValue());
	}
}
