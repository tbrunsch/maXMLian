import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

public class ParserTest
{
	public static void main(String[] args) throws IOException, XMLStreamException {
		Path path = Paths.get("C:\\User Data\\Workspace IntelliJ\\Own Projects\\maXMLian\\samples\\Sample with namespace.xml");
		Document document = FastXmlParser.readXml(Files.newInputStream(path));
		printTree(document, "");
		/*
		DocumentType doctype = document.getDoctype();
		System.out.println(doctype.getName());
		System.out.println(doctype.getInternalSubset());
		Element documentElement = document.getDocumentElement();
		System.out.println(documentElement.getAttributeNames());
		int a = 42;

		 */
	}

	private static void printTree(Node node, String indent) throws XMLStreamException {
		NodeType nodeType = node.getNodeType();
		switch (nodeType) {
			case DOCUMENT: {
				System.out.println(indent + "Start document");
				indent += " ";
				break;
			}

			case ELEMENT: {
				Element element = (Element) node;
				System.out.print(indent + "Start element " + element.getNodeName());
				System.out.print(", attributes:");
				Map<String, Attr> attributesByName = element.getAttributes();
				List<String> attributeNames = new ArrayList<>(attributesByName.keySet());
				Collections.sort(attributeNames);
				for (String attributeName : attributeNames) {
					Attr attribute = attributesByName.get(attributeName);
					System.out.print(" " + attribute.getName() + "=" + attribute.getValue());
				}
				System.out.println();
				indent += " ";

				break;
			}

			case TEXT: {
				System.out.println(indent + " Characters: " + node.getTextContent());
				break;
			}

			case ATTRIBUTE: {
				Attr attribute = (Attr) node;
				System.out.println(indent + " Attribute: " + attribute.getLocalName() + "=" + attribute.getValue());
				break;
			}

			case PROCESSING_INSTRUCTION: {
				System.out.println(indent + " Processing instruction");
				break;
			}

			case COMMENT: {
				System.out.println(indent + " Comment: " + node.getTextContent());
				break;
			}

			case DOCUMENT_TYPE: {
				System.out.println(indent + " DTD: " + ((DocumentType) node).getInternalSubset());
				break;
			}

			default: {
				System.out.println("Unknown node type: " + node.getClass());
				break;
			}
		}

		for (Node childNode : node.getChildren()) {
			printTree(childNode, indent);
		}

		switch (nodeType) {
			case DOCUMENT: {
				indent = indent.substring(0, indent.length() - 1);
				System.out.println(indent + "End document");
				break;
			}

			case ELEMENT: {
				indent = indent.substring(0, indent.length() - 1);
				System.out.println(indent + "End element " + ((Element) node).getNodeName());
				break;
			}

			default: {
				break;
			}
		}
	}
}
