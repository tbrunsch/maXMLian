package dd.kms.maxmlian.impl;

import javax.xml.stream.XMLInputFactory;
import java.util.Optional;

@FunctionalInterface
public interface XMLInputFactoryProvider
{
	Optional<XMLInputFactory> getXMLInputFactory();

	XMLInputFactoryProvider	XERCES		= fromClass("com.sun.xml.internal.stream.XMLInputFactoryImpl",	"Xerces");
	XMLInputFactoryProvider	WOODSTOX	= fromClass("com.ctc.wstx.stax.WstxInputFactory",				"Woodstox");
	XMLInputFactoryProvider	AALTO		= fromClass("com.fasterxml.aalto.stax.InputFactoryImpl",		"Aalto");
	XMLInputFactoryProvider	DEFAULT		= new XMLInputFactoryProvider()
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

	static XMLInputFactoryProvider fromClass(String factoryClassName, String description) {
		return new XMLInputFactoryProvider()
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
}
