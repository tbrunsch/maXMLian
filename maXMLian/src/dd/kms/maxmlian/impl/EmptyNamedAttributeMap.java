package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.Attr;
import dd.kms.maxmlian.api.NamedAttributeMap;

import java.util.Collections;
import java.util.Iterator;

class EmptyNamedAttributeMap implements NamedAttributeMap
{
	static final NamedAttributeMap	ATTRIBUTE_MAP	= new EmptyNamedAttributeMap();

	private EmptyNamedAttributeMap() { /* singleton */ }

	@Override
	public Attr get(String name) {
		return null;
	}

	@Override
	public Attr get(int index) {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public Iterator<Attr> iterator() {
		return Collections.emptyIterator();
	}
}
