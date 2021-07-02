abstract class CharacterDataImpl extends NodeImpl implements CharacterData
{
	private String	data;

	CharacterDataImpl(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void setData(String data) {
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
}
