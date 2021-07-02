class ProcessingInstructionImpl extends NodeImpl implements ProcessingInstruction
{
	private String	target;
	private String	data;

	ProcessingInstructionImpl(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initializeFromProcessingInstruction(javax.xml.stream.events.ProcessingInstruction processingInstruction) {
		super.initializePosition();
		target = processingInstruction.getTarget();
		data = processingInstruction.getData();
	}

	@Override
	public String getNodeName() {
		return target;
	}

	@Override
	public NodeType getNodeType() {
		return NodeType.PROCESSING_INSTRUCTION;
	}

	@Override
	public String getTarget() {
		return target;
	}

	@Override
	public String getData() {
		return data;
	}
}
