package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Namespace;

class NamespaceImpl extends AttrImpl implements Namespace
{
	@Override
	void initialize(String namespaceUri, String localName, String prefix, String value, String type) {
		if (localName == null) {
			/*
			 * Consider the namespace declaratione xmlns="http://www.w3.org/".
			 * - The StAX parser interprets "xmlns" as prefix
			 * - The DOM parser interprets "xmlns" as local name
			 */
			localName = prefix;
			prefix = null;
		}
		super.initialize(namespaceUri, localName, prefix, value, type);
	}

	@Override
	public boolean isDefaultNamespaceDeclaration() {
		return getLocalName() == null;
	}
}
