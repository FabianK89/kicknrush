package de.fmk.kicknrush.helper;


/**
 * Helper class for resource files.
 * @author Fabian Kiesl
 */
public class ResourceHelper {
	/**
	 * Get the relative path to a resource file laying in the resource package of the given class.
	 * @param clazz A class.
	 * @param fileName The name of a resource file; <i>[fileName].[fileEnding]</i>
	 * @return the relative path of the resource file.
	 */
	public static String getResourcePath(final Class<?> clazz, final String fileName) {
		final String        packageName;
		final StringBuilder pathBuilder;

		if (clazz == null)
			throw new IllegalArgumentException("The class must not be null.");

		if (fileName == null || !fileName.matches("[a-zA-Z0-9]+\\.[a-zA-Z0-9]+"))
			throw new IllegalArgumentException(fileName + " is not a correct file name.");

		pathBuilder = new StringBuilder("/");
		packageName = clazz.getPackage().getName();

		pathBuilder.append(packageName.replace(".", "/")).append("/").append(fileName);

		return new String(pathBuilder);
	}
}
