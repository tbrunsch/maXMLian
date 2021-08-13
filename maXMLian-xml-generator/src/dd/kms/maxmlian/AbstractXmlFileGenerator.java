package dd.kms.maxmlian;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;

abstract class AbstractXmlFileGenerator
{
	private static final String	INDENT_CHARACTERS	= "  ";

	private BufferedWriter	writer;
	private String			indent				= "";
	private int				numWrittenBytes		= 0;

	void reset(BufferedWriter writer) {
		numWrittenBytes = 0;
		indent = "";
		this.writer = writer;
	}

	int getNumWrittenBytes() {
		return numWrittenBytes;
	}

	int getDepth() {
		return indent.length() / INDENT_CHARACTERS.length();
	}

	private void openElement(String elementName) throws IOException {
		write(indent + "<" + elementName + ">");
		indent += INDENT_CHARACTERS;
	}

	private void openElement(String elementName, String attributeDefinitions) throws IOException {
		write(indent + "<" + elementName + " " + attributeDefinitions + ">");
		indent += INDENT_CHARACTERS;
	}

	private void closeElement(String elementName) throws IOException {
		indent = indent.substring(0, indent.length() - INDENT_CHARACTERS.length());
		write(indent + "</" + elementName + ">");
	}

	void write(String s) throws IOException {
		numWrittenBytes += s.length();
		writer.write(s + System.lineSeparator());
	}

	static class Attributes
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

	class Element implements Closeable
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
}
