package dd.kms.maxmlian.impl;

import javax.xml.namespace.QName;
import java.util.List;

class ImplUtils
{
	static String getQualifiedName(QName name) {
		String prefix = emptyToNull(name.getPrefix());
		String localPart = emptyToNull(name.getLocalPart());
		return prefix == null ? localPart : prefix + ":" + localPart;
	}

	static String emptyToNull(String s) {
		return s != null && s.length() == 0 ? null : s;
	}


	static <T> T get(List<T> list, int index) {
		return index < list.size() ? list.get(index) : null;
	}

	static <T> T set(List<T> list, int index, T value) {
		if (index < list.size()) {
			list.set(index, value);
		} else {
			while (list.size() < index) {
				list.add(null);
			}
			list.add(value);
		}
		return value;
	}
}
