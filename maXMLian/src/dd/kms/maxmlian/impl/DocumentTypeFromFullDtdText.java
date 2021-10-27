package dd.kms.maxmlian.impl;

import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.NotationDeclaration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of {@link dd.kms.maxmlian.api.DocumentType} for StAX parsers
 * like Xerces that return the whole DTD instead of the internal subset as text.
 */
class DocumentTypeFromFullDtdText extends AbstractDocumentTypeImpl
{
	private static final Pattern	DOCTYPE_NAME_PATTERN	= Pattern.compile("^\\s*<!DOCTYPE\\s+(\\w+).*", Pattern.DOTALL);
	private static final Pattern	IDS_PATTERN				= Pattern.compile("^\\s*<!DOCTYPE\\s+\\w+\\s+([^\\[]*).*", Pattern.DOTALL);
	private static final Pattern	INTERNAL_SUBSET_PATTERN	= Pattern.compile("^\\s*<!DOCTYPE\\s+[^\\[]*\\[(.*)\\]\\s*>$", Pattern.DOTALL);

	private static final String		PUBLIC					= "PUBLIC";
	private static final String		SYSTEM					= "SYSTEM";

	private String	documentTypeDeclaration;
	private String	publicId;
	private String	systemId;

	DocumentTypeFromFullDtdText(ExtendedXmlStreamReader eventReader, NodeFactory nodeFactory) {
		super(eventReader, nodeFactory);
	}

	void initialize(String documentTypeDeclaration, List<EntityDeclaration> entityDeclarations, List<NotationDeclaration> notationDeclarations) {
		super.initialize(entityDeclarations, notationDeclarations);
		this.documentTypeDeclaration = documentTypeDeclaration;
	}

	@Override
	public String getName() {
		Matcher matcher = DOCTYPE_NAME_PATTERN.matcher(documentTypeDeclaration);
		if (!matcher.matches() || matcher.groupCount() != 1) {
			// happens for Xerces when disabling DTD support
			return null;
		}
		return matcher.group(1);
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

	private void scanIds() {
		/*
		 * There seems to be no official way to obtain the public id and the system id
		 * via API, so we have to implement this logic ourselves.
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
