package dd.kms.maxmlian.api;

/**
 * Describes the extent to which DTDs (document type definitions) are supported.
 */
public enum DtdSupport
{
	/**
	 * DTDs are not supported at all. This is the safest way to protect against XML based
	 * attacks like XXE (XML eXternal Entity injection) attacks.
	 *
	 * @implNote This corresponds to
	 * <ul>
	 *     <li>
	 *         setting the property "javax.xml.stream.supportDTD" to false (StAX parser) or
	 *     </li>
	 *     <li>
	 *         setting the feature "http://apache.org/xml/features/disallow-doctype-decl"
	 *         to true (DOM parser).
	 *     </li>
	 * </ul>
	 */
	NONE,

	/**
	 * Only internal DTDs, but no external DTDs are supported. This reduces the risk for an
	 * XXE (XML eXternal Entity injection) attack, but does not fully protect against XML
	 * based attacks.
	 *
	 * @implNote This corresponds to
	 * <ul>
	 *     <li>
	 *         setting the property "javax.xml.stream.isSupportingExternalEntities" to
	 *         false (StAX parser) or
	 *     </li>
	 *     <li>
	 *         setting the features "http://xml.org/sax/features/external-general-entities",
	 *         "http://xml.org/sax/features/external-parameter-entities", and
	 *         "http://apache.org/xml/features/nonvalidating/load-external-dtd" to false
	 *         (DOM parser).
	 *     </li>
	 * </ul>
	 */
	INTERNAL,

	/**
	 * Both, internal and external DTDs, are supported. This option poses the highest
	 * risk for XML based attacks like XXE (XML eXternal Entity injection) attacks.
	 */
	INTERNAL_AND_EXTERNAL
}
