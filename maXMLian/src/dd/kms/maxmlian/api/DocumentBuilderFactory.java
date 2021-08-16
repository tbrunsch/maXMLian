package dd.kms.maxmlian.api;

/**
 * Creates a {@link DocumentBuilder} for parsing an XML document.<br/>
 * <br/>
 * Features:
 * <ul>
 *     <li>
 *         Instance reuse: When parsing a huge XML file, many node instances have to be created.
 *         For stream-based parsing this is usually not necessary because even if you hold a
 *         reference to some node, you cannot do much with that node if the parser has already
 *         proceeded. This is why it makes sense to reuse instances. Note that nodes are only
 *         reused if they have the same type and depth as the requested node instance. In particular,
 *         you can be sure that a parent and a child are always different instances.<br/>
 *         <br/>
 *         The {@code DocumentBuilderFactory} provides three levels of reuse:
 *         <ol>
 *             <li>
 *                 No reuse ({@link #withoutInstanceReuse()}; default behavior): All requested nodes
 *                 will be instantiated.
 *             </li>
 *             <li>
 *                 Delayed reuse ({@link #delayedInstanceReuse(int)}): For each node type and depth
 *                 a circular queue containing a user-defined number {@code k}, the so-called reuse
 *                 delay, of node instances is maintained. Nodes are reused in round-robin fashion.
 *                 This guarantees that one can hold references to the last {@code k} nodes of a given
 *                 depth and type.
 *             </li>
 *             <li>
 *                 Immediate reuse ({@link #immediateInstanceReuse()}: Nodes of the same type and depth
 *                 are immediately reused. The behavior is the same as calling {@link #delayedInstanceReuse(int)}
 *                 with reuse delay = 1. This minimizes the number of instantiations, but you cannot
 *                 hold references to siblings of the same type.
 *             </li>
 *         </ol>
 *     </li>
 * </ul>
 */
public interface DocumentBuilderFactory
{
	static DocumentBuilderFactory newInstance() {
		return new dd.kms.maxmlian.impl.DocumentBuilderFactoryImpl();
	}

	/**
	 * Node instances returned by the parser are always instantiated. Old instances will not be reused.
	 */
	DocumentBuilderFactory withoutInstanceReuse();

	/**
	 * Node instances will be reused after {@code reuseDelay} instances of the requested node type and depth
	 * have been created.
	 *
	 * @throws IllegalArgumentException if {@code reuseDelay < 1}
	 */
	DocumentBuilderFactory delayedInstanceReuse(int reuseDelay);

	/**
	 * Node instances will be reused if their type and depth matches the type and depth of the requested node instance.
	 */
	DocumentBuilderFactory immediateInstanceReuse();

	DocumentBuilderFactory setNamespaceAware(boolean namespaceAware);

	DocumentBuilder newDocumentBuilder();

	// void setIgnoringComments(boolean ignoreComments);
	// void setIgnoringElementContentWhitespace(boolean whitespace);
}
