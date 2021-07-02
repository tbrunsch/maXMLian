import java.util.List;

import javax.xml.stream.events.Characters;

class TextImpl extends CharacterDataImpl implements Text
{
	private boolean	isElementContentWhitespace;

	TextImpl(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initializeFromCharacters(Characters characters, List<Characters> additionalCharacters) {
		super.initializePosition();
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

	@Override
	public NodeType getNodeType() {
		return NodeType.TEXT;
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
