package dd.kms.maxmlian.api;

public interface DocumentBuilderFactory
{
	static DocumentBuilderFactory newInstance() {
		return new dd.kms.maxmlian.impl.DocumentBuilderFactoryImpl();
	}

	DocumentBuilder newDocumentBuilder();

	// void setIgnoringComments(boolean ignoreComments);
	// void setIgnoringElementContentWhitespace(boolean whitespace);
}
