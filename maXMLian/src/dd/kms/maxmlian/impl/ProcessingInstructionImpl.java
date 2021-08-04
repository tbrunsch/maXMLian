package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.ProcessingInstruction;

class ProcessingInstructionImpl extends NodeImpl implements ProcessingInstruction
{
	private String	target;
	private String	data;

	ProcessingInstructionImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initialize(String target, String data) {
		super.initialize();
		this.target = target;
		this.data = data;
	}

	@Override
	public String getNodeName() {
		return target;
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
