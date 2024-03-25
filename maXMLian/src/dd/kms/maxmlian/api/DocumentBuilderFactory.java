package dd.kms.maxmlian.api;

import javax.xml.stream.XMLInputFactory;

/**
 * Creates a {@link DocumentBuilder} for parsing an XML document.<br>
 * <br>
 * Features:
 * <ul>
 *     <li>
 *         Instance reuse: When parsing a huge XML file, many node instances have to
 *         be created. For stream-based parsing this is usually not reasonable because
 *         even if you hold a reference to some node, you cannot do much with that
 *         node if the parser has already proceeded. This is why it makes sense to
 *         reuse instances. Note that nodes are only reused if they have the same type
 *         and depth as the requested node instance. In particular, you can be sure
 *         that a parent and a child are always different instances.
 *     </li>
 *     <li>
 *         Namespace awareness: Specifies whether namespaces are considered separately
 *         or considered part of the local names.
 *     </li>
 *     <li>
 *         Normalization: Specifies whether adjacent text nodes are joined or not.
 *     </li>
 *     <li>
 *         DTD (document type definition) support: Specify to which extent DTDs are
 *         supported: No support, only internal DTDs, or internal and external DTDs.
 *     </li>
 * </ul>
 */
public interface DocumentBuilderFactory
{
	/**
	 * Creates a {@link DocumentBuilderFactory} that internally uses the first
	 * available StaX parser from the following list:
	 * <ol>
	 *     <li>Woodstox</li>
	 *     <li>Xerces</li>
	 *     <li>default StAX parser</li>
	 * </ol>
	 */
	static DocumentBuilderFactory newInstance() {
		return newInstance(XmlInputFactoryProvider.getFirstXmlInputFactory(
			XmlInputFactoryProvider.WOODSTOX,
			XmlInputFactoryProvider.XERCES,
			XmlInputFactoryProvider.DEFAULT
		));
	}

	/**
	 * Creates a {@link DocumentBuilderFactory} that internally uses the StAX parser
	 * returned by the specified {@link XMLInputFactory}. Note that maXMLian may modify
	 * this factory by setting certain properties.<br>
	 * You may use {@link XmlInputFactoryProvider} for creating and selecting an
	 * {@code XMLInputFactory}.
	 */
	static DocumentBuilderFactory newInstance(XMLInputFactory xmlInputFactory) {
		return new dd.kms.maxmlian.impl.DocumentBuilderFactoryImpl(xmlInputFactory);
	}

	/**
	 * Specify whether instances are reused or not (default). Instance reuse avoids
	 * unnecessary object instantiations, but maXMLian has less chances to detect
	 * incorrect (i.e., non-streamed) API usages.
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

	/**
	 * Specify the extent to which DTDs (document type definitions) are supported:
	 * No support (default), only support for internal DTDs, or support for internal
	 * and external DTDs. Note that the more DTDs are supported, the higher the risk
	 * for XML based attacks (see, e.g.,
	 * <a href="https://owasp.org/www-community/vulnerabilities/XML_External_Entity_(XXE)_Processing">XML External Entity (XXE) Processing</a>
	 * on OWASP).
	 */
	DocumentBuilderFactory dtdSupport(DtdSupport dtdSupport);

	DocumentBuilder newDocumentBuilder();
}
