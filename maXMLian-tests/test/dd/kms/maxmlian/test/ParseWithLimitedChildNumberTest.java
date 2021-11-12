package dd.kms.maxmlian.test;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;

/**
 * This test parses all XML files in the test resource folder with maXMLian and
 * with a DOM parser and compares the resulting trees. For all nodes, only a
 * certain number of children will be considered. For stream-based parsing,
 * this makes a difference compared to parsing the whole file
 * (cf. {@link ParseWholeFileTest}) because skipping XML events is implemented
 * differently than parsing them.
 */
class ParseWithLimitedChildNumberTest extends AbstractFileParsingTest
{
	private Map<Integer, Integer>	maxNumberOfChildrenPerDepth	= new HashMap<>();

	@Override
	void prepareTest(Document domDocument, boolean considerOnlyChildElements) {
		determineMaximumNumberOfChildren(domDocument, 0, considerOnlyChildElements);
	}

	@Override
	int getNumberOfChildrenToParse(int depth) {
		return Math.max(maxNumberOfChildrenPerDepth.getOrDefault(depth, 0) - 2, 1);
	}

	private void determineMaximumNumberOfChildren(Node domNode, int depth, boolean considerOnlyChildElements) {
		NodeList children = domNode.getChildNodes();
		int numChildren = children.getLength();
		int numConsideredChildren = 0;
		for (int i = 0; i < numChildren; i++) {
			Node domChild = children.item(i);
			boolean considerChild = !considerOnlyChildElements || domChild.getNodeType() == Node.ELEMENT_NODE;
			if (considerChild) {
				determineMaximumNumberOfChildren(domChild, depth + 1, considerOnlyChildElements);
				numConsideredChildren++;
			}
		}
		updateMaxNumberOfChildren(depth, numConsideredChildren);
	}

	private void updateMaxNumberOfChildren(int depth, int numChildren) {
		if (numChildren == 0) {
			return;
		}
		Integer maxNumChildren = maxNumberOfChildrenPerDepth.get(depth);
		if (maxNumChildren == null || maxNumChildren < numChildren) {
			maxNumberOfChildrenPerDepth.put(depth, numChildren);
		}
	}
}
