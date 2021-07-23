package dd.kms.maxmlian;

import dd.kms.maxmlian.reflections.ClassX;
import dd.kms.maxmlian.reflections.FieldX;
import dd.kms.maxmlian.reflections.MethodX;
import dd.kms.maxmlian.reflections.PackageX;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class generates a large XML file with a relatively deep structure and roughly
 * the specified file size.
 */
public class LargeXmlFileGenerator
{
	private static final String	ELEMENT_NAME_PACKAGE			= "package";
	private static final String	ELEMENT_NAME_PACKAGES			= "packages";
	private static final String	ELEMENT_NAME_CLASS				= "class";
	private static final String	ELEMENT_NAME_CLASSES			= "classes";
	private static final String	ELEMENT_NAME_FIELD				= "field";
	private static final String	ELEMENT_NAME_FIELDS				= "fields";
	private static final String	ELEMENT_NAME_METHOD				= "method";
	private static final String	ELEMENT_NAME_METHODS			= "methods";
	private static final String	ELEMENT_NAME_TYPE				= "type";
	private static final String	ELEMENT_NAME_PARAMETER_TYPES	= "parametertypes";
	private static final String	ELEMENT_NAME_RETURN_TYPE		= "returntype";
	private static final String	ELEMENT_NAME_EXCEPTION_TYPES	= "exceptiontypes";

	private final long			targetFileSize;
	private BufferedWriter		writer;
	private int					numWrittenBytes				= 0;
	private String				indent						= "";
	private int					nestedClassElementCounter	= 0;

	public LargeXmlFileGenerator(long targetFileSize) {
		this.targetFileSize = targetFileSize;
	}

	public synchronized void generate(Path file) throws IOException {
		numWrittenBytes = 0;
		indent = "";
		try (BufferedWriter internalWriter = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
			writer = internalWriter;
			write("<?xml version=\"1.0\"?>");
			PackageX pck = new ClassX(java.lang.String.class).getPackage().getParentPackage();
			writePackage(pck);
		}
	}

	private void writePackage(PackageX pck) throws IOException {
		String attributeDefinitions = new Attributes()
			.add("name", pck.getName())
			.getDefinitions();
		try (Element ignored = new Element(ELEMENT_NAME_PACKAGE, attributeDefinitions)) {
			writeSubpackages(pck);
			writeClasses(pck);
		}
	}

	private void writeSubpackages(PackageX pck) throws IOException {
		try (Element ignored = new Element(ELEMENT_NAME_PACKAGES)) {
			for (PackageX subpackage : pck.getSubpackages()) {
				writePackage(subpackage);
			}
		}
	}

	private void writeClasses(PackageX pck) throws IOException {
		try (Element ignored = new Element(ELEMENT_NAME_CLASSES)) {
			for (ClassX clazz : pck.getClasses()) {
				writeClass(clazz);
			}
		}
	}

	private void writeClass(ClassX clazz) throws IOException {
		String attributeDefinitions = new Attributes()
			.add("name", clazz.getName())
			.add("visibility", clazz.getAccessModifier())
			.add("static", clazz.isStatic())
			.add("abstract", clazz.isAbstract())
			.add("final", clazz.isFinal())
			.add("interface", clazz.isInterface())
			.getDefinitions();
		try (Element ignored = new ClassElement(attributeDefinitions)) {
			if (nestedClassElementCounter >= 3) {
				return;
			}
			if (numWrittenBytes >= targetFileSize) {
				return;
			}
			writeFields(clazz);
			writeMethods(clazz);
		}
	}

	private void writeFields(ClassX clazz) throws IOException {
		try (Element ignored = new Element(ELEMENT_NAME_FIELDS)) {
			for (FieldX field : clazz.getFields()) {
				writeField(field);
			}
		}
	}

	private void writeField(FieldX field) throws IOException {
		String attributeDefinitions = new Attributes()
			.add("name", field.getName())
			.add("visibility", field.getAccessModifier())
			.add("static", field.isStatic())
			.add("final", field.isFinal())
			.add("transient", field.isTransient())
			.add("volatile", field.isVolatile())
			.getDefinitions();
		try (Element ignored = new Element(ELEMENT_NAME_FIELD, attributeDefinitions)) {
			writeType(field);
		}
	}

	private void writeType(FieldX field) throws IOException {
		try (Element ignored = new Element(ELEMENT_NAME_TYPE)) {
			writeClass(field.getType());
		}
	}

	private void writeMethods(ClassX clazz) throws IOException {
		try (Element ignored = new Element(ELEMENT_NAME_METHODS)) {
			for (MethodX method : clazz.getMethods()) {
				writeMethod(method);
			}
		}
	}

	private void writeMethod(MethodX method) throws IOException {
		String attributeDefinitions = new Attributes()
			.add("name", method.getName())
			.add("visibility", method.getAccessModifier())
			.add("static", method.isStatic())
			.add("final", method.isFinal())
			.add("abstract", method.isAbstract())
			.add("synchronized", method.isSynchronized())
			.getDefinitions();
		try (Element ignored = new Element(ELEMENT_NAME_METHOD, attributeDefinitions)) {
			writeArgumentTypes(method);
			writeReturnType(method);
			writeExceptionTypes(method);
		}
	}

	private void writeArgumentTypes(MethodX method) throws IOException {
		try (Element ignored = new Element(ELEMENT_NAME_PARAMETER_TYPES)) {
			for (ClassX parameterType : method.getParameterTypes()) {
				writeClass(parameterType);
			}
		}
	}
	
	private void writeReturnType(MethodX method) throws IOException {
		try (Element ignored = new Element(ELEMENT_NAME_RETURN_TYPE)) {
			writeClass(method.getReturnType());
		}
	}
	
	private void writeExceptionTypes(MethodX method) throws IOException {
		try (Element ignored = new Element(ELEMENT_NAME_EXCEPTION_TYPES)) {
			for (ClassX exceptionType : method.getExceptionTypes()) {
				writeClass(exceptionType);
			}
		}
	}

	private void openElement(String elementName) throws IOException {
		write(indent + "<" + elementName + ">");
		indent += "  ";
	}

	private void openElement(String elementName, String attributeDefinitions) throws IOException {
		write(indent + "<" + elementName + " " + attributeDefinitions + ">");
		indent += "  ";
	}

	private void closeElement(String elementName) throws IOException {
		indent = indent.substring(0, indent.length() - 2);
		write(indent + "</" + elementName + ">");
	}

	private void write(String s) throws IOException {
		numWrittenBytes += s.length();
		writer.write(s + System.lineSeparator());
	}

	private static class Attributes
	{
		private final StringBuilder builder	= new StringBuilder();

		Attributes add(String attributeName, Object attributeValue) {
			if (builder.length() > 0) {
				builder.append(" ");
			}
			builder.append(attributeName).append("=").append('"').append(attributeValue.toString()).append('"');
			return this;
		}

		String getDefinitions() {
			return builder.toString();
		}
	}

	private class Element implements Closeable
	{
		private final String elementName;

		Element(String elementName) throws IOException {
			this.elementName = elementName;
			openElement(elementName);
		}

		Element(String elementName, String attributeDefinitions) throws IOException {
			this.elementName = elementName;
			openElement(elementName, attributeDefinitions);
		}

		@Override
		public void close() throws IOException {
			closeElement(elementName);
		}
	}

	private class ClassElement extends Element
	{
		ClassElement(String attributeDefinitions) throws IOException {
			super(ELEMENT_NAME_CLASS, attributeDefinitions);
			nestedClassElementCounter++;
		}

		@Override
		public void close() throws IOException {
			super.close();
			nestedClassElementCounter--;
		}
	}
}