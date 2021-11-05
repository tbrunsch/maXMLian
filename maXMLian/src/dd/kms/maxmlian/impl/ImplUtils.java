package dd.kms.maxmlian.impl;

import java.util.List;

class ImplUtils
{
	static String getQualifiedName(String localName, String prefix) {
		return prefix == null ? localName : prefix + ":" + localName;
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
