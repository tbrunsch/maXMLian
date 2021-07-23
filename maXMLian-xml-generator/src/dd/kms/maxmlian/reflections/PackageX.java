package dd.kms.maxmlian.reflections;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PackageX
{
	/**
	 * Collection of class names for which {@link Class#forName(String)} does not succeed
	 */
	private static final List<String>	CLASS_NAMES_TO_SKIP	= Arrays.asList("java.util.prefs.WindowsPreferences");

	private final String	name;
	private final Path		path;

	public PackageX(String name, Path path) {
		this.name = name;
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public List<PackageX> getSubpackages() {
		List<PackageX> subpackages = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path path : stream) {
				if (Files.isDirectory(path)) {
					String directoryName = path.getFileName().toString().replace("/", "");
					PackageX subpackage = new PackageX(name + "." + directoryName, path);
					subpackages.add(subpackage);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return subpackages;
	}

	public List<ClassX> getClasses() {
		List<ClassX> classes = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
			for (Path path : stream) {
				if (Files.isRegularFile(path)) {
					String className = name + "." + path.getFileName().toString();
					if (!className.endsWith(".class")) {
						continue;
					}
					className = className.substring(0, className.length() - ".class".length());
					if (CLASS_NAMES_TO_SKIP.contains(className)) {
						continue;
					}
					Class<?> classForName;
					try {
						classForName = Class.forName(className);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						continue;
					}
					ClassX clazz = new ClassX(classForName);
					classes.add(clazz);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classes;
	}

	public PackageX getParentPackage() {
		int lastIndexOfDot = name.lastIndexOf(".");
		if (lastIndexOfDot < 0) {
			return null;
		}
		String parentName = name.substring(0, lastIndexOfDot);
		Path directory = path.getParent();
		return new PackageX(parentName, directory);
	}

	@Override
	public String toString() {
		return name;
	}
}
