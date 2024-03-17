package dd.kms.maxmlian.api;

/**
 * Creates a {@link DocumentBuilder} for parsing an XML document.<br/>
 * <br/>
 * Features:
 * <ul>
 *     <li>
 *         Instance reuse: When parsing a huge XML file, many node instances have to be created.
 *         For stream-based parsing this is usually not reasonable because even if you hold a
 *         reference to some node, you cannot do much with that node if the parser has already
 *         proceeded. This is why it makes sense to reuse instances. Note that nodes are only
 *         reused if they have the same type and depth as the requested node instance. In particular,
 *         you can be sure that a parent and a child are always different instances.
 *     </li>
 * </ul>
 */
public interface DocumentBuilderFactory
{
	static DocumentBuilderFactory newInstance() {
		return new dd.kms.maxmlian.impl.DocumentBuilderFactoryImpl();
	}

	/**
	 * Specify whether instances are reused or not (default). Instance reuse avoids unnecessary object instantiations,
	 * but maXMLian has less chances to detect incorrect (i.e., non-streamed) API usages.
	 */
	DocumentBuilderFactory reuseInstances(boolean reuseInstances);

	DocumentBuilderFactory namespaceAware(boolean namespaceAware);

	DocumentBuilderFactory normalize(boolean normalize);

	DocumentBuilder newDocumentBuilder();
}
