package dd.kms.maxmlian.impl;

import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;
import java.util.List;

class DocumentTypeImpl extends AbstractDocumentTypeImpl
{
	static final int	VALID_FLAG_DOCUMENT_TYPE_NAME	= 0x1;
	static final int	VALID_FLAG_PUBLIC_ID			= 0x2;
	static final int	VALID_FLAG_SYSTEM_ID			= 0x4;

	private String	documentTypeName;
	private String	publicId;
	private String	systemId;

	/**
	 * Indicates whether {@link #documentTypeName}, {@link #publicId}, and/or
	 * {@link #systemId} are valid.
	 */
	private int		validFlags;
	private String	internalSubset;

	DocumentTypeImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initialize(String documentTypeName, String publicId, String systemId, int validFlags, String internalSubset, List<EntityDeclaration> entityDeclarations, List<NotationDeclaration> notationDeclarations) {
		super.initialize(entityDeclarations, notationDeclarations);
		this.documentTypeName = documentTypeName;
		this.publicId = publicId;
		this.systemId = systemId;
		this.validFlags = validFlags;
		this.internalSubset = internalSubset;
	}

	@Override
	public String getName() {
		if (isValid(VALID_FLAG_DOCUMENT_TYPE_NAME)) {
			return documentTypeName;
		}
		throw new UnsupportedOperationException("The internally used StAX parser does not provide a way to retrieve the name of the document type definition");
	}

	@Override
	public String getPublicId() {
		if (isValid(VALID_FLAG_PUBLIC_ID)) {
			return publicId;
		}
		throw new UnsupportedOperationException("The internally used StAX parser does not provide a way to retrieve the public id of the document type definition");
	}

	@Override
	public String getSystemId() {
		if (isValid(VALID_FLAG_SYSTEM_ID)) {
			return systemId;
		}
		throw new UnsupportedOperationException("The internally used StAX parser does not provide a way to retrieve the system id of the document type definition");
	}

	@Override
	public String getInternalSubset() {
		return internalSubset;
	}

	private boolean isValid(int bit) {
		return (validFlags & bit) != 0;
	}
}
