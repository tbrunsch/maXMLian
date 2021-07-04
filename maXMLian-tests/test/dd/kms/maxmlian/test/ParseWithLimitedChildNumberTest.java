package dd.kms.maxmlian.test;

import java.nio.file.Path;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This test parses all XML files in the test resource folder with maXMLian and
 * with a DOM parser and compares the resulting trees. For all nodes, only a
 * certain number of children will be considered. For stream-based parsing,
 * this makes a difference compared to parsing the whole file
 * (cf. {@link ParseWholeFileTest}) because skipping XML events is implemented
 * differently than parsing them.
 */
public class ParseWithLimitedChildNumberTest extends AbstractFileParsingTest
{
	private int maxNumberOfChildrenToParse;

	public ParseWithLimitedChildNumberTest(Path xmlFile) {
		super(xmlFile);
	}

	@Override
	void prepareTest(Document domDocument) {
		int maxNumberOfChildren = determineMaximumNumberOfChildren(domDocument);
		maxNumberOfChildrenToParse = Math.max(0, maxNumberOfChildren - 2);
	}

	@Override
	int getNumberOfChildrenToParse(int depth) {
		return maxNumberOfChildrenToParse - 2;
	}

	private static int determineMaximumNumberOfChildren(Node domNode) {
		NodeList children = domNode.getChildNodes();
		int numChildren = children.getLength();
		int maxNumberOfChildren = numChildren;
		for (int i = 0; i < numChildren; i++) {
			Node domChild = children.item(i);
			maxNumberOfChildren = Math.max(maxNumberOfChildren, determineMaximumNumberOfChildren(domChild));
		}
		return maxNumberOfChildren;
	}
}
