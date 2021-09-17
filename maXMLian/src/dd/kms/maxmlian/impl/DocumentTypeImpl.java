package dd.kms.maxmlian.impl;

import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;
import java.util.List;

class DocumentTypeImpl extends AbstractDocumentTypeImpl
{
	private String	documentTypeName;
	private String	publicId;
	private String	systemId;
	private String	internalSubset;

	DocumentTypeImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initialize(String documentTypeName, String publicId, String systemId, String internalSubset, List<EntityDeclaration> entityDeclarations, List<NotationDeclaration> notationDeclarations) {
		super.initialize(entityDeclarations, notationDeclarations);
		this.documentTypeName = documentTypeName;
		this.publicId = publicId;
		this.systemId = systemId;
		this.internalSubset = internalSubset;
	}

	@Override
	public String getName() {
		return documentTypeName;
	}

	@Override
	public String getPublicId() {
		return publicId;
	}

	@Override
	public String getSystemId() {
		return systemId;
	}

	@Override
	public String getInternalSubset() {
		return internalSubset;
	}
}
