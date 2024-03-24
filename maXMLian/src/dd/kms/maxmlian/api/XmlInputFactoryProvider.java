package dd.kms.maxmlian.api;

import javax.xml.stream.XMLInputFactory;
import java.util.Optional;

@FunctionalInterface
public interface XmlInputFactoryProvider
{
	Optional<XMLInputFactory> getXMLInputFactory();

	XmlInputFactoryProvider XERCES		= fromClass("com.sun.xml.internal.stream.XMLInputFactoryImpl",	"Xerces");
	XmlInputFactoryProvider WOODSTOX	= fromClass("com.ctc.wstx.stax.WstxInputFactory",				"Woodstox");
	XmlInputFactoryProvider AALTO		= fromClass("com.fasterxml.aalto.stax.InputFactoryImpl",		"Aalto");
	XmlInputFactoryProvider DEFAULT		= new XmlInputFactoryProvider()
	{
		@Override
		public Optional<XMLInputFactory> getXMLInputFactory() {
			return Optional.of(XMLInputFactory.newFactory());
		}

		@Override
		public String toString() {
			return "default StAX parser";
		}
	};

	static XmlInputFactoryProvider fromClass(String factoryClassName, String description) {
		return new XmlInputFactoryProvider()
		{
			@Override
			public Optional<XMLInputFactory> getXMLInputFactory() {
				try {
					Class<?> factoryClass = Class.forName(factoryClassName);
					return Optional.of((XMLInputFactory) factoryClass.newInstance());
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
					return Optional.empty();
				}
			}

			@Override
			public String toString() {
				return description;
			}
		};
	}

	/**
	 * Returns the first available {@link XMLInputFactory} returned by one of the specified
	 * {@link XmlInputFactoryProvider}s.
	 *
	 * @throws IllegalStateException if none of the specified {@code XmlInputFactoryProvider}s
	 *                               returned an {@code XMLInputFactory}.
	 */
	static XMLInputFactory getFirstXmlInputFactory(XmlInputFactoryProvider... xmlInputFactoryProviders) throws IllegalStateException {
		for (XmlInputFactoryProvider xmlInputFactoryProvider : xmlInputFactoryProviders) {
			XMLInputFactory factory = xmlInputFactoryProvider.getXMLInputFactory().orElse(null);
			if (factory != null) {
				return factory;
			}
		}
		throw new IllegalStateException("Cannot instantiate an XMLInputFactory");
	}
}
