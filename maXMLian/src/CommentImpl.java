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

	@Override
	public NodeType getNodeType() {
		return NodeType.COMMENT;
	}
}
