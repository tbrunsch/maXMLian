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

abstract class AbstractDocumentTypeImpl extends NodeImpl implements DocumentType
{
	private List<EntityDeclaration>		entityDeclarations;
	private List<NotationDeclaration>	notationDeclarations;

	AbstractDocumentTypeImpl(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initialize(List<EntityDeclaration> entityDeclarations, List<NotationDeclaration> notationDeclarations) {
		super.initialize();
		this.entityDeclarations = entityDeclarations;
		this.notationDeclarations = notationDeclarations;
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
			String entityName = entityDeclaration.getName();
			// only consider general entities, not parameter entities
			boolean isParameterEntity = entityName.startsWith("%");
			if (isParameterEntity) {
				continue;
			}
			Entity entity = new EntityImpl(entityDeclaration, nextSibling);
			entityMap.put(entityName, entity);
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
	public String getNodeName() {
		return getName();
	}
}
