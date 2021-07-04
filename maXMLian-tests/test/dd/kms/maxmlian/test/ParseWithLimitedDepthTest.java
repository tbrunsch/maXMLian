package dd.kms.maxmlian.test;

import java.nio.file.Path;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This test parses all XML files in the test resource folder with maXMLian and
 * with a DOM parser and compares the resulting trees. All nodes starting at a
 * certain depth are ignored. For stream-based parsing, this makes a difference
 * compared to parsing the whole file (cf. {@link ParseWholeFileTest}) because
 * skipping XML events is implemented differently than parsing them.
 */
public class ParseWithLimitedDepthTest extends AbstractFileParsingTest
{
	private int maxDepthToConsider;

	public ParseWithLimitedDepthTest(Path xmlFile) {
		super(xmlFile);
	}

	@Override
	void prepareTest(Document domDocument) {
		int maxDepth = determineMaxDepth(domDocument);
		maxDepthToConsider = maxDepth / 2;
	}

	@Override
	int getNumberOfChildrenToParse(int depth) {
		return depth <= maxDepthToConsider ? Integer.MAX_VALUE : 0;
	}

	private static int determineMaxDepth(Node domNode) {
		NodeList children = domNode.getChildNodes();
		int numChildren = children.getLength();
		final int initialDepth;
		short nodeType = domNode.getNodeType();
		switch (nodeType) {
			case Node.ELEMENT_NODE:
				initialDepth = 2;	// +1 for attributes +1 for text node of each attribute
				break;
			case Node.DOCUMENT_TYPE_NODE:
				initialDepth = 1;	// +1 for entities and notations
				break;
			default:
				initialDepth = 0;
				break;
		}
		int depth = initialDepth;
		for (int i = 0; i < numChildren; i++) {
			Node domChild = children.item(i);
			depth = Math.max(depth, 1 + determineMaxDepth(domChild));
		}
		return depth;
	}
}
