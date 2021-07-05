package dd.kms.maxmlian.impl;

import java.util.List;

import javax.xml.stream.events.Characters;

import dd.kms.maxmlian.api.Text;

class TextImpl extends CharacterDataImpl implements Text
{
	private boolean	isElementContentWhitespace;

	TextImpl(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initializeFromCharacters(Characters characters, List<Characters> additionalCharacters) {
		super.initialize();
		String data = characters.getData();
		this.isElementContentWhitespace = characters.isIgnorableWhiteSpace();
		if (additionalCharacters != null) {
			for (Characters additionalCharacter : additionalCharacters) {
				data += additionalCharacter.getData();
				this.isElementContentWhitespace &= additionalCharacter.isIgnorableWhiteSpace();
			}
		}
		setData(data);
	}

	void initializeFromData(String data) {
		super.initialize();
		setData(data);
		// TODO: How to set isElementContentWhitespace in this case?
		this.isElementContentWhitespace = false;
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
