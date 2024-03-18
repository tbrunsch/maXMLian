package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.CharacterData;

abstract class CharacterDataImpl extends NodeImpl implements CharacterData
{
	private String	data;

	CharacterDataImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initialize(String data) {
		super.initialize();
		this.data = data;
	}

	@Override
	public String getData() {
		return data;
	}

	@Override
	public String getNodeValue() {
		return data;
	}

	@Override
	public String getTextContent() {
		return data;
	}

	@Override
	void appendTextContentTo(StringBuilder builder) {
		builder.append(data);
	}

}
