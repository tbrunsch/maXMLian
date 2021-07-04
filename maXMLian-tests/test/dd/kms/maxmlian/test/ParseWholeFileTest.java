package dd.kms.maxmlian.test;

import java.nio.file.Path;

import org.w3c.dom.Document;

/**
 * This test parses all XML files in the test resource folder with maXMLian and
 * with a DOM parser and compares the resulting trees. Although this sounds like
 * the ultimate unit test, it does not cover use cases where the document is only
 * parsed partially while some other parts are skipped.
 */
public class ParseWholeFileTest extends AbstractFileParsingTest
{
	public ParseWholeFileTest(Path xmlFile) {
		super(xmlFile);
	}

	@Override
	void prepareTest(Document domDocument) {
		// nothing to do
	}

	@Override
	int getNumberOfChildrenToParse(int depth) {
		return Integer.MAX_VALUE;
	}
}
