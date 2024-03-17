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
	/**
	 * Creates an instance of {@link DocumentBuilderFactory}. It will use the StAX parser
	 * of the first {@link javax.xml.stream.XMLInputFactory} provided by the specified
	 * {@code xmlInputFactoryProviders}. If no such providers are specified, then an
	 * internal prioritized list of {@link XmlInputFactoryProvider}s is used.
	 *
	 * @throws IllegalStateException	if no {@code XMLInputFactory} can be created
	 */
	static DocumentBuilderFactory newInstance(XmlInputFactoryProvider... xmlInputFactoryProviders) throws IllegalStateException {
		return xmlInputFactoryProviders.length > 0
				? new dd.kms.maxmlian.impl.DocumentBuilderFactoryImpl(xmlInputFactoryProviders)
				: new dd.kms.maxmlian.impl.DocumentBuilderFactoryImpl(
					XmlInputFactoryProvider.WOODSTOX,
					XmlInputFactoryProvider.XERCES,
					XmlInputFactoryProvider.DEFAULT
				);
	}

	/**
	 * Specify whether instances are reused or not (default). Instance reuse avoids unnecessary object instantiations,
	 * but maXMLian has less chances to detect incorrect (i.e., non-streamed) API usages.
	 */
	DocumentBuilderFactory reuseInstances(boolean reuseInstances);

	/**
	 * Specify whether namespaces should be considered (default) or not. If this option
	 * is disabled, then namespaces are considered part of the local names.
	 *
	 * @throws IllegalStateException	if the underlying StAX parser does not support it
	 */
	DocumentBuilderFactory namespaceAware(boolean namespaceAware) throws IllegalStateException;

	/**
	 * Specify whether adjacent text nodes should be joined or not (default). While
	 * normalization is convenient, it can potentially create large text nodes,
	 * undermining the streaming approach of maXMLian.
	 */
	DocumentBuilderFactory normalize(boolean normalize);

	DocumentBuilder newDocumentBuilder();
}
