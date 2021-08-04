package dd.kms.maxmlian.impl;

import dd.kms.maxmlian.api.DocumentType;
import dd.kms.maxmlian.api.Entity;
import dd.kms.maxmlian.api.Notation;

import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DocumentTypeImpl extends NodeImpl implements DocumentType
{
	private static final Pattern	DOCTYPE_NAME_PATTERN	= Pattern.compile("^\\s*<!DOCTYPE\\s+(\\w+).*", Pattern.DOTALL);
	private static final Pattern	INTERNAL_SUBSET_PATTERN	= Pattern.compile("^\\s*<!DOCTYPE\\s+[^\\[]*\\[(.*)\\]\\s*>$", Pattern.DOTALL);

	private String						documentTypeDeclaration;
	private List<EntityDeclaration>		entityDeclarations;
	private List<NotationDeclaration>	notationDeclarations;

	DocumentTypeImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initialize(String documentTypeDeclaration, List<EntityDeclaration> entityDeclarations, List<NotationDeclaration> notationDeclarations) {
		super.initialize();
		this.documentTypeDeclaration = documentTypeDeclaration;
		this.entityDeclarations = entityDeclarations;
		this.notationDeclarations = notationDeclarations;
	}

	@Override
	public String getName() {
		Matcher matcher = DOCTYPE_NAME_PATTERN.matcher(documentTypeDeclaration);
		if (!matcher.matches() || matcher.groupCount() != 1) {
			throw new IllegalStateException("Cannot extract name from document type declaration: '" + documentTypeDeclaration + "'");
		}
		return matcher.group(1);
	}

	@Override
	public Map<String, Entity> getEntities() {
		if (entityDeclarations == null || entityDeclarations.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, Entity> entityMap = new LinkedHashMap<>();
		Entity nextSibling = null;
		for (int i = entityDeclarations.size() - 1; i >= 0; i--) {
			EntityDeclaration entityDeclaration = entityDeclarations.get(i);
			Entity entity = new EntityImpl(entityDeclaration, nextSibling);
			entityMap.put(entity.getNodeName(), entity);
			nextSibling = entity;
		}
		return entityMap;
	}

	@Override
	public Map<String, Notation> getNotations() {
		if (notationDeclarations == null || notationDeclarations.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, Notation> notationMap = new LinkedHashMap<>();
		Notation nextSibling = null;
		for (int i = notationDeclarations.size() - 1; i >= 0; i--) {
			NotationDeclaration notationDeclaration = notationDeclarations.get(i);
			Notation notation = new NotationImpl(notationDeclaration, nextSibling);
			notationMap.put(notation.getNodeName(), notation);
			nextSibling = notation;
		}
		return notationMap;
	}

	@Override
	public String getPublicId() {
		return null;
	}

	@Override
	public String getSystemId() {
		return null;
	}

	@Override
	public String getInternalSubset() {
		Matcher matcher = INTERNAL_SUBSET_PATTERN.matcher(documentTypeDeclaration);
		return matcher.matches() && matcher.groupCount() == 1
				? matcher.group(1).trim()
				: null;
	}

	@Override
	public String getNodeName() {
		return getName();
	}
}
