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
		for (Characters additionalCharacter : additionalCharacters) {
			data += additionalCharacter.getData();
			this.isElementContentWhitespace &= additionalCharacter.isIgnorableWhiteSpace();
		}
		setData(data);
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
