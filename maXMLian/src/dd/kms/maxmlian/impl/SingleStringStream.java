package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.StringStream;
import dd.kms.maxmlian.api.XmlException;

class SingleStringStream implements StringStream
{
	private final String	string;
	private boolean			initialState	= true;

	SingleStringStream(String string) {
		this.string = string;
	}

	@Override
	public String next() throws XmlException {
		if (initialState) {
			initialState = false;
			return string;
		}
		return null;
	}
}
