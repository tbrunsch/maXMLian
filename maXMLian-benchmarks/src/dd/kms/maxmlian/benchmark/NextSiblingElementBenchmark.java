package dd.kms.maxmlian.benchmark;

import dd.kms.maxmlian.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This benchmark compares the running time when using {@link Node#getFirstChildElement()} ()} and {@link Node#getNextSiblingElement()}
 * instead of {@link Node#getFirstChild()} and {@link Node#getNextSibling()} and skipping all nodes that are not elements.
 */
public class NextSiblingElementBenchmark
{
	private static final int	NUM_TRIALS	= 10;

	/**
	 * @param args	An array containing the required benchmark parameters.<br/>
	 *              {@code args[0]} contains the full path of the benchmark file. You can use the {@code LargeXmlFileGenerator} to generate such a file.
	 */
	public static void main(String[] args) throws IOException, XmlException {
		if (args.length != 1) {
			throw new IllegalArgumentException("There must be exactly 1 argument: the full path of the benchmark XML file");
		}
		String benchmarkXmlFilePath = args[0];
		Path benchmarkXmlFile = Paths.get(benchmarkXmlFilePath);
		if (!Files.exists(benchmarkXmlFile)) {
			throw new IllegalArgumentException("The benchmark XML file is does not exist");
		}
		if (!Files.isRegularFile(benchmarkXmlFile)) {
			throw new IllegalArgumentException("The benchmark XML file is no regular file");
		}

		LongValueStatistics filterRequestedChildrenTime = new LongValueStatistics();
		LongValueStatistics requestedChildElementsTime = new LongValueStatistics();
		for (int trial = 0; trial <= NUM_TRIALS; trial++) {
			if (trial == 0) {
				System.out.println("Dummy trial");
			} else {
				System.out.println("Trial " + trial + " of " + NUM_TRIALS);
			}
			long timestamp1 = System.currentTimeMillis();
			traverseElements(benchmarkXmlFile, ElementConsiderationMode.FILTER_REQUESTED_CHILDREN);
			long timestamp2 = System.currentTimeMillis();
			traverseElements(benchmarkXmlFile, ElementConsiderationMode.REQUEST_CHILD_ELEMENTS);
			long timestamp3 = System.currentTimeMillis();

			if (trial > 0) {
				filterRequestedChildrenTime.addValue(timestamp2 - timestamp1);
				requestedChildElementsTime.addValue(timestamp3 - timestamp2);
			}
		}

		System.out.println("Time for filtering requested children:       " + filterRequestedChildrenTime.getAverageValue()/1000 + "s");
		System.out.println("Time for directly requesting child elements: " + requestedChildElementsTime.getAverageValue()/1000 + "s");
	}

	private static void traverseElements(Path xmlFile, ElementConsiderationMode elementConsiderationMode) throws IOException, XmlException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance().reuseInstances(true);
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		Document document = documentBuilder.parse(Files.newInputStream(xmlFile));
		switch (elementConsiderationMode) {
			case FILTER_REQUESTED_CHILDREN:
				traverseElementsByFilteringChildren(document);
				break;
			case REQUEST_CHILD_ELEMENTS:
				traverseElementsByRequestingElements(document);
				break;
			default:
				throw new UnsupportedOperationException("Unexpected element consideration mode: " + elementConsiderationMode);
		}
	}

	private static void traverseElementsByFilteringChildren(Node node) throws XmlException {
		for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
			if (child.getNodeType() == NodeType.ELEMENT) {
				traverseElementsByFilteringChildren(child);
			}
		}
	}

	private static void traverseElementsByRequestingElements(Node node) throws XmlException {
		for (Element childElement = node.getFirstChildElement(); childElement != null; childElement = childElement.getNextSiblingElement()) {
			traverseElementsByRequestingElements(childElement);
		}
	}

	private enum ElementConsiderationMode
	{
		FILTER_REQUESTED_CHILDREN,
		REQUEST_CHILD_ELEMENTS
	}
}
