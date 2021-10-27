package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.DocumentBuilder;
import dd.kms.maxmlian.api.DocumentBuilderFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentBuilderFactoryImpl implements DocumentBuilderFactory
{
	private int		reuseDelay		= ImplUtils.INSTANCE_REUSE_NONE;
	private boolean namespaceAware	= false;

	private List<XMLInputFactoryProvider> xmlInputFactoryProviders	= Arrays.asList(
		XMLInputFactoryProvider.WOODSTOX,
		XMLInputFactoryProvider.XERCES,
		XMLInputFactoryProvider.DEFAULT
	);

	@Override
	public DocumentBuilderFactory withoutInstanceReuse() {
		this.reuseDelay = ImplUtils.INSTANCE_REUSE_NONE;
		return this;
	}

	@Override
	public DocumentBuilderFactory delayedInstanceReuse(int reuseDelay) {
		if (reuseDelay < 1) {
			throw new IllegalArgumentException("The reuse delay must be at least 1, but is " + reuseDelay);
		}
		this.reuseDelay = reuseDelay;
		return this;
	}

	@Override
	public DocumentBuilderFactory immediateInstanceReuse() {
		this.reuseDelay = ImplUtils.INSTANCE_REUSE_IMMEDIATE;
		return this;
	}

	@Override
	public DocumentBuilderFactory setNamespaceAware(boolean namespaceAware) {
		this.namespaceAware = namespaceAware;
		return this;
	}

	/**
	 * This method is not part of the API since the internal StAX parser is not meant
	 * to be configurable by the user. However, this method is internally used for
	 * testing maXMLian with different StAX parsers.
	 */
	public DocumentBuilderFactory setXMLInputFactoryProviders(XMLInputFactoryProvider... xmlInputFactoryProviders) {
		this.xmlInputFactoryProviders = new ArrayList<>();
		for (XMLInputFactoryProvider xmlInputFactoryProvider : xmlInputFactoryProviders) {
			this.xmlInputFactoryProviders.add(xmlInputFactoryProvider);
		}
		return this;
	}

	@Override
	public DocumentBuilder newDocumentBuilder() {
		return new DocumentBuilderImpl(reuseDelay, namespaceAware, xmlInputFactoryProviders);
	}
}
