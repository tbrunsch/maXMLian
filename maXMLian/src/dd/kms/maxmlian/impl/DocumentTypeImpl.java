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
	private static final Pattern	IDS_PATTERN				= Pattern.compile("^\\s*<!DOCTYPE\\s+\\w+\\s+([^\\[]*).*", Pattern.DOTALL);
	private static final Pattern	INTERNAL_SUBSET_PATTERN	= Pattern.compile("^\\s*<!DOCTYPE\\s+[^\\[]*\\[(.*)\\]\\s*>$", Pattern.DOTALL);

	private static final String		PUBLIC					= "PUBLIC";
	private static final String		SYSTEM					= "SYSTEM";

	private String						documentTypeDeclaration;
	private List<EntityDeclaration>		entityDeclarations;
	private List<NotationDeclaration>	notationDeclarations;
	private String						publicId;
	private String						systemId;

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
		if (systemId == null) {
			scanIds();
		}
		return publicId;
	}

	@Override
	public String getSystemId() {
		if (systemId == null) {
			scanIds();
		}
		return systemId;
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

	private void scanIds() {
		/*
		 * com.sun.org.apache.xerces.internal.impl.XMLDocumentScannerImpl
		 * has stored the public ID and the system ID in the fields
		 * fDoctypePublicId and fDoctypeSystemId, but there seems to be
		 * no way to retrieve these values. Hence, we have to implement
		 * this logic ourselves.
		 */
		Matcher matcher = IDS_PATTERN.matcher(documentTypeDeclaration);
		if (!matcher.matches() || matcher.groupCount() != 1) {
			return;
		}
		String ids = matcher.group(1).trim();
		Tokenizer tokenizer = new Tokenizer(ids);
		String publicOrSystem = tokenizer.readWord();
		if (PUBLIC.equalsIgnoreCase(publicOrSystem)) {
			tokenizer.skipSpaces();
			publicId = tokenizer.readQuotedString();
			tokenizer.skipSpaces();
			systemId = tokenizer.readQuotedString();
		} else if (SYSTEM.equalsIgnoreCase(publicOrSystem)) {
			tokenizer.skipSpaces();
			systemId = tokenizer.readQuotedString();
		}
	}

	private static class Tokenizer
	{
		private static final Pattern	WORD_PATTERN	= Pattern.compile("^(\\w+).*$", Pattern.DOTALL);

		private final String	s;
		private int 			pos;

		Tokenizer(String s) {
			this.s = s;
		}

		String readWord() {
			Matcher matcher = WORD_PATTERN.matcher(s.substring(pos));
			if (!matcher.matches() || matcher.groupCount() != 1) {
				return null;
			}
			String word = matcher.group(1);
			pos += word.length();
			return word;
		}

		void skipSpaces() {
			while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
				pos++;
			}
		}

		String readQuotedString() {
			if (pos == s.length()) {
				return null;
			}
			char c = s.charAt(pos);
			if (c == '"' || c == '\"') {
				pos++;
				return readUntilEndOfQuote(c);
			}
			return null;
		}

		private String readUntilEndOfQuote(char quoteChar) {
			StringBuilder builder = new StringBuilder();
			while (pos < s.length()) {
				char c = s.charAt(pos++);
				if (c == quoteChar) {
					return builder.toString();
				}
				builder.append(c);
			}
			// missing end of quote
			return null;
		}
	}
}
