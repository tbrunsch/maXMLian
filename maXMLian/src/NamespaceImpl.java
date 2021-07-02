class NamespaceImpl extends AttrImpl implements Namespace
{
	private boolean	defaultNamespaceDeclaration;

	void initializeFromNamespace(javax.xml.stream.events.Namespace namespace) {
		initializeFromAttribute(namespace);
		this.defaultNamespaceDeclaration = namespace.isDefaultNamespaceDeclaration();
	}

	@Override
	public boolean isDefaultNamespaceDeclaration() {
		return defaultNamespaceDeclaration;
	}
}
