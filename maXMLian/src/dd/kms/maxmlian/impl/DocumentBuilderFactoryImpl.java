package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.DocumentBuilder;
import dd.kms.maxmlian.api.DocumentBuilderFactory;

import javax.xml.stream.XMLInputFactory;

public class DocumentBuilderFactoryImpl implements DocumentBuilderFactory
{
	private final XMLInputFactory	xmlInputFactory;
	private boolean	reuseInstances	= false;

	public DocumentBuilderFactoryImpl(XMLInputFactory xmlInputFactory) throws IllegalStateException {
		this.xmlInputFactory = xmlInputFactory;
		xmlInputFactory.setProperty(XMLInputFactory.IS_VALIDATING, false);
	}

	@Override
	public DocumentBuilderFactory reuseInstances(boolean reuseInstances) {
		this.reuseInstances = reuseInstances;
		return this;
	}

	@Override
	public DocumentBuilderFactory namespaceAware(boolean namespaceAware) throws IllegalStateException {
		try {
			xmlInputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, namespaceAware);
		} catch (IllegalStateException e) {
			String action = namespaceAware ? "enable" : "disable";
			throw new IllegalStateException("Cannot " + action + " namespace awareness because the internal StAX parser does not support it.");
		}
		return this;
	}

	@Override
	public DocumentBuilderFactory normalize(boolean normalize) {
		xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, normalize);
		return this;
	}

	@Override
	public DocumentBuilder newDocumentBuilder() {
		return new DocumentBuilderImpl(xmlInputFactory, reuseInstances);
	}
}
