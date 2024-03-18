package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Text;

class TextImpl extends CharacterDataImpl implements Text
{
	private boolean	isElementContentWhitespace;

	TextImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initialize(String data, boolean isElementContentWhitespace) {
		super.initialize(data);
		this.isElementContentWhitespace = isElementContentWhitespace;
	}

	@Override
	public String getNodeName() {
		return "#text";
	}

	@Override
	public boolean isElementContentWhitespace() {
		return isElementContentWhitespace;
	}
}
