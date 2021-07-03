package dd.kms.maxmlian.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;

import dd.kms.maxmlian.api.DocumentType;
import dd.kms.maxmlian.api.Entity;
import dd.kms.maxmlian.api.NodeType;
import dd.kms.maxmlian.api.Notation;

class DocumentTypeImpl extends NodeImpl implements DocumentType
{
	private static final Pattern	DOCTYPE_NAME_PATTERN	= Pattern.compile("^\\s*<!DOCTYPE\\s+(\\w+).*", Pattern.DOTALL);
	private static final Pattern	INTERNAL_SUBSET_PATTERN	= Pattern.compile("^\\s*<!DOCTYPE\\s+[^\\[]*\\[(.*)\\]\\s*>$", Pattern.DOTALL);

	private DTD	dtd;

	DocumentTypeImpl(ExtendedXmlEventReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initializeFromDtd(DTD dtd) {
		initializePosition();
		this.dtd = dtd;
	}

	@Override
	public String getName() {
		String documentTypeDeclaration = dtd.getDocumentTypeDeclaration();
		Matcher matcher = DOCTYPE_NAME_PATTERN.matcher(documentTypeDeclaration);
		if (!matcher.matches() || matcher.groupCount() != 1) {
			throw new IllegalStateException("Cannot extract name from document type declaration: '" + documentTypeDeclaration + "'");
		}
		return matcher.group(1);
	}

	@Override
	public Map<String, Entity> getEntities() {
		List<EntityDeclaration> entityDeclarations = dtd.getEntities();
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
		List<NotationDeclaration> notationDeclarations = dtd.getNotations();
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
		String documentTypeDeclaration = dtd.getDocumentTypeDeclaration();
		Matcher matcher = INTERNAL_SUBSET_PATTERN.matcher(documentTypeDeclaration);
		if (!matcher.matches() || matcher.groupCount() != 1) {
			return null;
		}
		return matcher.group(1).trim();
	}

	@Override
	public String getNodeName() {
		return getName();
	}
}
