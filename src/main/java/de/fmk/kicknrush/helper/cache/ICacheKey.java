package de.fmk.kicknrush.helper.cache;

/**
 * Interface for the keys that are used in the caches.
 */
public interface ICacheKey {
	/**
	 * @return the class of the value stored by this key.
	 */
	Class<?> getValueClass();

	/**
	 * @return the key string to identify.
	 */
	String getKey();
}
