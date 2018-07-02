package de.fmk.kicknrush.helper;


import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for resource files.
 * @author Fabian Kiesl
 */
public class ResourceHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ResourceHelper.class);


	private ResourceHelper() {}


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


	/**
	 * Load the app icons.
	 * @return a list with the app icons.
	 */
	public static List<Image> getAppIcons() {
		final List<Image> appImages;
		final String      packagePath;
		final String[]    imageNames;

		appImages   = new ArrayList<>();
		packagePath = "/de/fmk/kicknrush/images/";
		imageNames  = new String[] { "app_icon16x16.png", "app_icon32x32.png", "app_icon64x64.png" };

		for (final String name : imageNames) {
			try (InputStream is = ResourceHelper.class.getResourceAsStream(packagePath.concat(name))) {
				appImages.add(new Image(is));
			}
			catch (IOException ioex) {
				LOGGER.error("Could not load the image with name '{}'.", name, ioex);
			}
		}

		return appImages;
	}
}
