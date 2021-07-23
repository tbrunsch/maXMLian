package dd.kms.maxmlian.reflections;

import java.lang.reflect.Modifier;

public enum AccessModifier
{
	PRIVATE			("private"),
	PACKAGE_PRIVATE	("package private"),
	PROTECTED		("protected"),
	PUBLIC			("public");

	private final String	stringRepresentation;

	AccessModifier(String stringRepresentation) {
		this.stringRepresentation = stringRepresentation;
	}

	static AccessModifier fromModifiers(int modifier) {
		if (Modifier.isPrivate(modifier)) {
			return PRIVATE;
		}
		if (Modifier.isProtected(modifier)) {
			return PROTECTED;
		}
		if (Modifier.isPublic(modifier)) {
			return PUBLIC;
		}
		return PACKAGE_PRIVATE;
	}

	@Override
	public String toString() {
		return stringRepresentation;
	}
}
