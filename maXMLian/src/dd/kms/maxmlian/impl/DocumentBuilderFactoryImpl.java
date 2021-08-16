package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.DocumentBuilder;
import dd.kms.maxmlian.api.DocumentBuilderFactory;

public class DocumentBuilderFactoryImpl implements DocumentBuilderFactory
{
	private int		reuseDelay		= ImplUtils.INSTANCE_REUSE_NONE;
	private boolean namespaceAware	= false;

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

	@Override
	public DocumentBuilder newDocumentBuilder() {
		return new DocumentBuilderImpl(reuseDelay, namespaceAware);
	}
}
