package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.StringStream;

class EmptyStringStream implements StringStream
{
	static final StringStream	STRING_STREAM	= new EmptyStringStream();

	@Override
	public String next() {
		return null;
	}
}
