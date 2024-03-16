package dd.kms.maxmlian.test;

import dd.kms.maxmlian.LargeTextNodeXmlFileGenerator;
import dd.kms.maxmlian.LargeXmlFileGenerator;
import dd.kms.maxmlian.api.NodeType;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

class TestUtils
{
	private static final Path	LARGE_XML_FILE				= Paths.get(System.getProperty("user.home")).resolve("LargeXmlFile.xml");
	private static final Path	LARGE_TEXT_NODE_XML_FILE	= Paths.get(System.getProperty("user.home")).resolve("LargeTextNodeXmlFile.xml");
	private static final long	LARGE_FILE_SIZE				= 20 * (1 << 20);	// 20 MB

	static List<Path> getTemporaryXmlFiles() throws IOException {
		return Arrays.asList(
			getLargeXmlFile(),
			getLargeTextNodeXmlFile()
		);
	}

	private static Path getLargeXmlFile() throws IOException {
		if (!Files.exists(LARGE_XML_FILE)) {
			LargeXmlFileGenerator generator = new LargeXmlFileGenerator(LARGE_FILE_SIZE);
			generator.generate(LARGE_XML_FILE);
		}
		return LARGE_XML_FILE;
	}

	private static Path getLargeTextNodeXmlFile() throws IOException {
		if (!Files.exists(LARGE_TEXT_NODE_XML_FILE)) {
			LargeTextNodeXmlFileGenerator generator = new LargeTextNodeXmlFileGenerator(LARGE_FILE_SIZE);
			generator.generate(LARGE_TEXT_NODE_XML_FILE);
		}
		return LARGE_TEXT_NODE_XML_FILE;
	}

	static void deleteTemporaryXmlFiles() throws IOException {
		if (Files.exists(LARGE_XML_FILE)) {
			Files.delete(LARGE_XML_FILE);
		}
		if (Files.exists(LARGE_TEXT_NODE_XML_FILE)) {
			Files.delete(LARGE_TEXT_NODE_XML_FILE);
		}
	}

	static Path getResourceDirectory() {
		URL resourceDirectoryUrl = TestUtils.class.getResource("/");
		URI resourceDirectoryUri = null;
		try {
			resourceDirectoryUri = resourceDirectoryUrl.toURI();
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Resource URL '" + resourceDirectoryUrl + "' is not valid URI");
		}
		return Paths.get(resourceDirectoryUri);
	}

	static NodeType getNodeType(short domNodeType) {
		switch (domNodeType) {
			case org.w3c.dom.Node.ATTRIBUTE_NODE:
				return NodeType.ATTRIBUTE;
			case org.w3c.dom.Node.DOCUMENT_NODE:
				return NodeType.DOCUMENT;
			case org.w3c.dom.Node.COMMENT_NODE:
				return NodeType.COMMENT;
			case org.w3c.dom.Node.TEXT_NODE:
				return NodeType.TEXT;
			case org.w3c.dom.Node.ELEMENT_NODE:
				return NodeType.ELEMENT;
			case org.w3c.dom.Node.CDATA_SECTION_NODE:
				return NodeType.CDATA_SECTION;
			case org.w3c.dom.Node.DOCUMENT_TYPE_NODE:
				return NodeType.DOCUMENT_TYPE;
			case org.w3c.dom.Node.ENTITY_NODE:
				return NodeType.ENTITY;
			case org.w3c.dom.Node.NOTATION_NODE:
				return NodeType.NOTATION;
			case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
				return NodeType.PROCESSING_INSTRUCTION;
			default:
				throw new UnsupportedOperationException("DOM node type " + domNodeType + " is currently not supported");
		}
	}
}
