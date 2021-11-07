package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Attr;
import dd.kms.maxmlian.api.NamedAttributeMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class NamedAttributeMapImpl implements NamedAttributeMap
{
	private AttrImpl			lastAttr;
	private final List<Attr>	attributes	= new ArrayList<>();

	void clear() {
		lastAttr = null;
		attributes.clear();
	}

	void add(AttrImpl attr) {
		attr.setPrevSibling(lastAttr);
		attributes.add(attr);
		lastAttr = attr;
	}

	@Override
	public Attr get(String name) {
		return null;
	}

	@Override
	public Attr get(int index) {
		return attributes.get(index);
	}

	@Override
	public int size() {
		return attributes.size();
	}

	@Override
	public Iterator<Attr> iterator() {
		return attributes.iterator();
	}
}
