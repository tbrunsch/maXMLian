package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.DocumentBuilder;
import dd.kms.maxmlian.api.DocumentBuilderFactory;

public class DocumentBuilderFactoryImpl implements DocumentBuilderFactory
{
	@Override
	public DocumentBuilder newDocumentBuilder() {
		return new DocumentBuilderImpl();
	}
}
