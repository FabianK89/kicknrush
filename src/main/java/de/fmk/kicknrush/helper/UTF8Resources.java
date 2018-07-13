package de.fmk.kicknrush.helper;

import java.nio.charset.Charset;
import java.util.ResourceBundle;


/**
 * Stores the resources and encodes texts to UTF-8.
 *
 * @author FabianK
 */
public class UTF8Resources {
	private final ResourceBundle resources;


	/**
	 * Constructor.
	 * @param bundle The resource bundle.
	 */
	public UTF8Resources(ResourceBundle bundle) {
		resources = bundle;
	}


	/**
	 * Gets a encoded string for the given key from this resource bundle.
	 * @param key The key of a string.
	 * @return the string encoded to UTF-8.
	 */
	public String get(final String key) {
		return encodeToUTF8(resources.getString(key));
	}


	/**
	 * Gets a formatted encoded string for the given key with the given arguments from this resource bundle.
	 * @param key The key of a string.
	 * @param args Arguments to format the string.
	 * @return the formatted string encoded to UTF-8.
	 */
	public String get(final String key, String... args) {
		if (key == null)
			return null;

		if (args == null || args.length == 0)
			return get(key);

		return String.format(encodeToUTF8(resources.getString(key)), args);
	}


	private String encodeToUTF8(final String text) {
		final byte[] bytes;

		if (text == null || text.isEmpty())
			return text;

		bytes = text.getBytes(Charset.forName("ISO-8859-1"));

		return new String(bytes, Charset.forName("UTF-8"));
	}
}
