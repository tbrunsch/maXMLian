package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.DocumentBuilder;
import dd.kms.maxmlian.api.DocumentBuilderFactory;
import dd.kms.maxmlian.api.XmlInputFactoryProvider;

import javax.xml.stream.XMLInputFactory;

public class DocumentBuilderFactoryImpl implements DocumentBuilderFactory
{
	private final XMLInputFactory	factory;
	private boolean	reuseInstances	= false;

	public DocumentBuilderFactoryImpl(XmlInputFactoryProvider... xmlInputFactoryProviders) throws IllegalStateException {
		this.factory = getFirstXmlInputFactory(xmlInputFactoryProviders);
		factory.setProperty(XMLInputFactory.IS_VALIDATING, false);
	}

	@Override
	public DocumentBuilderFactory reuseInstances(boolean reuseInstances) {
		this.reuseInstances = reuseInstances;
		return this;
	}

	@Override
	public DocumentBuilderFactory namespaceAware(boolean namespaceAware) throws IllegalStateException {
		try {
			factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, namespaceAware);
		} catch (IllegalStateException e) {
			String action = namespaceAware ? "enable" : "disable";
			throw new IllegalStateException("Cannot " + action + " namespace awareness because the internal StAX parser does not support it.");
		}
		return this;
	}

	@Override
	public DocumentBuilderFactory normalize(boolean normalize) {
		factory.setProperty(XMLInputFactory.IS_COALESCING, normalize);
		return this;
	}

	@Override
	public DocumentBuilder newDocumentBuilder() {
		return new DocumentBuilderImpl(factory, reuseInstances);
	}

	private static XMLInputFactory getFirstXmlInputFactory(XmlInputFactoryProvider... xmlInputFactoryProviders) throws IllegalStateException {
		XMLInputFactory factory = null;
		for (XmlInputFactoryProvider xmlInputFactoryProvider : xmlInputFactoryProviders) {
			factory = xmlInputFactoryProvider.getXMLInputFactory().orElse(null);
			if (factory != null) {
				break;
			}
		}
		if (factory == null) {
			throw new IllegalStateException("Cannot instantiate an XMLInputFactory");
		}
		return factory;
	}
}
