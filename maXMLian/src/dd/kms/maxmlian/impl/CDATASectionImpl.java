package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.CDATASection;

class CDATASectionImpl extends TextImpl implements CDATASection
{
	CDATASectionImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	@Override
	public String getNodeName() {
		return "#cdata-section";
	}
}
