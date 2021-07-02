class CDATASectionImpl extends TextImpl implements CDATASection
{
	CDATASectionImpl(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	@Override
	public NodeType getNodeType() {
		return NodeType.CDATA_SECTION;
	}

	@Override
	public String getNodeName() {
		return "#cdata-section";
	}
}
