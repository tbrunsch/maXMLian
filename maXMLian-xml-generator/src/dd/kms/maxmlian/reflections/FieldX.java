package dd.kms.maxmlian.reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FieldX
{
	private final Field	field;

	public FieldX(Field field) {
		this.field = field;
	}

	public String getName() {
		return field.getName();
	}

	public AccessModifier getAccessModifier() {
		return AccessModifier.fromModifiers(field.getModifiers());
	}

	public boolean isFinal() {
		return Modifier.isFinal(field.getModifiers());
	}

	public boolean isStatic() {
		return Modifier.isStatic(field.getModifiers());
	}

	public boolean isVolatile() {
		return Modifier.isVolatile(field.getModifiers());
	}

	public boolean isTransient() {
		return Modifier.isTransient(field.getModifiers());
	}

	public ClassX getDeclaringClass() {
		return new ClassX(field.getDeclaringClass());
	}

	public ClassX getType() {
		return new ClassX(field.getType());
	}

	@Override
	public String toString() {
		return getName();
	}
}
