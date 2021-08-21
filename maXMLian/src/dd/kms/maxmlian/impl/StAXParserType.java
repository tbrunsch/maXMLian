package dd.kms.maxmlian.impl;

import javax.xml.stream.XMLInputFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Consumer;

public enum StAXParserType
{
	XERCES	("com.sun.xml.internal.stream.XMLInputFactoryImpl",	factory -> {}),
	WOODSTOX("com.ctc.wstx.stax.WstxInputFactory",				StAXParserType::configureForSpeed),
	AALTO	("com.fasterxml.aalto.stax.InputFactoryImpl",		StAXParserType::configureForSpeed);

	private final String					factoryClassName;
	private final Consumer<XMLInputFactory>	factoryConfigurator;

	StAXParserType(String factoryClassName, Consumer<XMLInputFactory> factoryConfigurator) {
		this.factoryClassName = factoryClassName;
		this.factoryConfigurator = factoryConfigurator;
	}

	public Optional<XMLInputFactory> createXmlInputFactory() {
		XMLInputFactory factory;
		try {
			Class<?> factoryClass = Class.forName(factoryClassName);
			factory = (XMLInputFactory) factoryClass.newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			return Optional.empty();
		}
		factoryConfigurator.accept(factory);
		return Optional.of(factory);
	}

	private static void configureForSpeed(XMLInputFactory factory) {
		Class<?> factoryClass = factory.getClass();
		try {
			Method configureForSpeedMethod = factoryClass.getMethod("configureForSpeed");
			configureForSpeedMethod.invoke(factory);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
			/* do nothing */
		}
	}
}
