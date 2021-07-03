package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.CDATASection;
import dd.kms.maxmlian.api.NodeType;

class CDATASectionImpl extends TextImpl implements CDATASection
{
	CDATASectionImpl(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	@Override
	public String getNodeName() {
		return "#cdata-section";
	}
}
