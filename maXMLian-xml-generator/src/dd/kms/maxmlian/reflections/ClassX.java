package dd.kms.maxmlian.reflections;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class ClassX
{
	private final Class<?> clazz;

	public ClassX(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getName() {
		return getName(clazz);
	}

	public String getSimpleName() {
		return clazz.getSimpleName();
	}

	public AccessModifier getAccessModifier() {
		return AccessModifier.fromModifiers(clazz.getModifiers());
	}

	public boolean isFinal() {
		return Modifier.isFinal(clazz.getModifiers());
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(clazz.getModifiers());
	}

	public boolean isInterface() {
		return Modifier.isInterface(clazz.getModifiers());
	}

	public boolean isStatic() {
		return Modifier.isStatic(clazz.getModifiers());
	}

	public List<FieldX> getFields() {
		List<FieldX> fields = new ArrayList<>();
		for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
			Field[] declaredFields = c.getDeclaredFields();
			for (Field declaredField : declaredFields) {
				FieldX field = new FieldX(declaredField);
				fields.add(field);
			}
		}
		return fields;
	}

	public List<MethodX> getMethods() {
		List<MethodX> methods = new ArrayList<>();
		Map<String, List<Method>> methodsByName = new HashMap<>();
		Set<Class<?>> typesToConsider = new LinkedHashSet<>();
		for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
			typesToConsider.add(c);
		}
		collectImplementedInterfaces(clazz, typesToConsider);
		for (Class<?> type : typesToConsider) {
			Method[] declaredMethods = type.getDeclaredMethods();
			for (Method declaredMethod : declaredMethods) {
				String name = declaredMethod.getName();
				List<Method> methodsWithSameName = methodsByName.get(name);
				if (methodsWithSameName == null) {
					methodsWithSameName = new ArrayList<>();
					methodsByName.put(name, methodsWithSameName);
				}
				if (!methodIsOverriddenBy(declaredMethod, methodsWithSameName)) {
					MethodX method = new MethodX(declaredMethod);
					methods.add(method);
					methodsWithSameName.add(declaredMethod);
				}
			}
		}
		return methods;
	}

	public PackageX getPackage() {
		String className = clazz.getName();
		String classFile = className.replace(".", "/") + ".class";
		URL resourceUrl = ClassLoader.getSystemClassLoader().getResource(classFile);
		if (resourceUrl == null) {
			throw new IllegalStateException("Could not find resource for class '" + clazz + "'");
		}
		URI resourceUri;
		try {
			resourceUri = resourceUrl.toURI();
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Invalid URI: '" + resourceUrl + "'", e);
		}
		if ("jar".equals(resourceUri.getScheme())) {
			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			try {
				FileSystems.newFileSystem(resourceUri, env);
			} catch (IOException e) {
				throw new IllegalStateException("Could not open file system for URI '" + resourceUri + "'", e);
			}
		}
		Path path;
		try {
			path = Paths.get(resourceUri);
		} catch (FileSystemNotFoundException e) {
			throw new IllegalStateException("Cannot get path for URI '" + resourceUri + "'", e);
		}
		Path directory = path.getParent();
		Package pack = clazz.getPackage();
		return new PackageX(pack.getName(), directory);
	}

	private void collectImplementedInterfaces(Class<?> clazz, Set<Class<?>> implementedInterfaces) {
		Class<?>[] interfaces = clazz.getInterfaces();
		for (Class<?> interf : interfaces) {
			implementedInterfaces.add(interf);
			collectImplementedInterfaces(interf, implementedInterfaces);
		}
	}

	private boolean methodIsOverriddenBy(Method method, List<Method> methods) {
		for (Method otherMethod : methods) {
			if (methodIsOverriddenBy(method, otherMethod)) {
				return true;
			}
		}
		return false;
	}

	private boolean methodIsOverriddenBy(Method method, Method otherMethod) {
		/*
		 * Assumptions:
		 * - both methods have the same name
		 * - method declared in a superclass of the declaring class of otherMethod
		 */
		int methodModifiers = method.getModifiers();
		int otherMethodModifiers = otherMethod.getModifiers();
		if (Modifier.isPrivate(methodModifiers) || Modifier.isFinal(methodModifiers) || Modifier.isPrivate(otherMethodModifiers)) {
			return false;
		}

		Class<?>[] argumentClasses = method.getParameterTypes();
		Class<?>[] otherArgumentClasses = otherMethod.getParameterTypes();
		if (argumentClasses.length != otherArgumentClasses.length) {
			return false;
		}
		for (int i = 0; i < argumentClasses.length; i++) {
			if (argumentClasses[i] != otherArgumentClasses[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return getName();
	}

	private static String getName(Class<?> clazz) {
		Class<?> componentType = clazz.getComponentType();
		if (componentType != null) {
			// array type
			return getName(componentType) + "[]";
		}
		return clazz.getName();
	}
}

