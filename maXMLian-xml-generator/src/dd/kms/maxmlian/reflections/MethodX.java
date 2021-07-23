package dd.kms.maxmlian.reflections;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MethodX
{
	private final Method	method;

	public MethodX(Method method) {
		this.method = method;
	}

	public String getName() {
		return method.getName();
	}

	public AccessModifier getAccessModifier() {
		return AccessModifier.fromModifiers(method.getModifiers());
	}

	public boolean isFinal() {
		return Modifier.isFinal(method.getModifiers());
	}

	public boolean isStatic() {
		return Modifier.isStatic(method.getModifiers());
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(method.getModifiers());
	}

	public boolean isSynchronized() {
		return Modifier.isSynchronized(method.getModifiers());
	}

	public List<ClassX> getParameterTypes() {
		Class<?>[] parameterTypes = method.getParameterTypes();
		return Arrays.stream(parameterTypes).map(ClassX::new).collect(Collectors.toList());
	}

	public ClassX getReturnType() {
		return new ClassX(method.getReturnType());
	}

	public List<ClassX> getExceptionTypes() {
		Class<?>[] exceptionTypes = method.getExceptionTypes();
		return Arrays.stream(exceptionTypes).map(ClassX::new).collect(Collectors.toList());
	}

	public ClassX getDeclaringClass() {
		return new ClassX(method.getDeclaringClass());
	}

	@Override
	public String toString() {
		return getReturnType()
				+ " "
				+ getName()
				+ "("
				+ getParameterTypes().stream().map(Object::toString).collect(Collectors.joining(", "))
				+ ")";
	}
}
