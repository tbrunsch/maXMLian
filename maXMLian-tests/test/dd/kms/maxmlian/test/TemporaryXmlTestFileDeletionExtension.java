package dd.kms.maxmlian.test;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.IOException;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

/**
 * This extension registers a shutdown hook that deletes the large XML file provided by
 * {@link TestUtils}.
 */
public class TemporaryXmlTestFileDeletionExtension implements BeforeAllCallback, ExtensionContext.Store.CloseableResource
{
	@Override
	public void beforeAll(ExtensionContext extensionContext) {
		extensionContext.getRoot().getStore(GLOBAL).put("Delete large XML file shutdown hook", this);
	}

	@Override
	public void close() throws IOException {
		TestUtils.deleteTemporaryXmlFiles();
	}
}
