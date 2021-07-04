package dd.kms.maxmlian.impl;

import javax.xml.namespace.QName;

class XmlUtils
{
	static String getQualifiedName(QName name) {
		String prefix = emptyToNull(name.getPrefix());
		String localPart = emptyToNull(name.getLocalPart());
		return prefix == null ? localPart : prefix + ":" + localPart;
	}

	static String emptyToNull(String s) {
		return s != null && s.length() == 0 ? null : s;
	}
}
