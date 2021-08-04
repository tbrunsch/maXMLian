package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Text;

class TextImpl extends CharacterDataImpl implements Text
{
	private boolean	isElementContentWhitespace;

	TextImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initialize(String data, StringBuilder additionalDataBuilder, boolean isElementContentWhitespace) {
		super.initialize();
		setData(additionalDataBuilder.length() == 0 ? data : data + additionalDataBuilder.toString());
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

	@Override
	public String getTextContent() {
		return getNodeValue();
	}

	@Override
	void appendTextContentTo(StringBuilder builder) {
		builder.append(getNodeValue());
	}
}
