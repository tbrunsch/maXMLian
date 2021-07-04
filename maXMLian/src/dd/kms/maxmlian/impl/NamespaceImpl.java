package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Namespace;

class NamespaceImpl extends AttrImpl implements Namespace
{
	private boolean	defaultNamespaceDeclaration;

	NamespaceImpl(NodeFactory nodeFactory) {
		super(nodeFactory);
	}

	void initializeFromNamespace(javax.xml.stream.events.Namespace namespace, int depth) {
		initializeFromAttribute(namespace, depth);
		this.defaultNamespaceDeclaration = namespace.isDefaultNamespaceDeclaration();
	}

	@Override
	public boolean isDefaultNamespaceDeclaration() {
		return defaultNamespaceDeclaration;
	}

	@Override
	public String getName() {
		return getLocalPart() != null ? super.getName() : getLocalName();
	}

	@Override
	public String getPrefix() {
		return getLocalPart() != null ? super.getPrefix() : null;
	}

	@Override
	public String getLocalName() {
		return getLocalPart() != null ? super.getLocalName() : super.getPrefix();
	}

	/**
	 * This method is required for special handling of namespace declaration xmlns="...".
	 * The StAX parser considers "xmlns" as prefix and "" as local name, but in DOM
	 * "xmlns" is expected to be the name as well as the local name and the prefix is
	 * expected to be null.
	 */
	private String getLocalPart() {
		return XmlUtils.emptyToNull(attribute.getName().getLocalPart());
	}
}
