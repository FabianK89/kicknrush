package de.fmk.kicknrush.helper.cache;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

import java.util.Map;


/**
 * Interface for a cache.
 *
 * @author FabianK
 */
public interface ICache {
	/**
	 * Clear the cache.
	 */
	void clear();

	/**
	 * Get the boolean property for binding purposes.
	 * @param key The key of the value.
	 * @return the property for this key.
	 * @throws java.lang.IllegalArgumentException if the stored value is not a boolean value.
	 */
	BooleanProperty getBooleanProperty(ICacheKey key);

	/**
	 * Read a boolean value from the cache.
	 * @param key The key of the value.
	 * @param fallback Fallback value if no value could be found for the key.
	 * @return the stored value or the fallback value.
	 */
	boolean getBooleanValue(ICacheKey key, boolean fallback);

	/**
	 * Read a boolean value from the cache.
	 * @param key The key of the value.
	 * @return the stored value or <code>false</code>.
	 */
	boolean getBooleanValue(ICacheKey key);

	/**
	 * Get the double property for binding purposes.
	 * @param key The key of the value.
	 * @return the property for this key.
	 * @throws java.lang.IllegalArgumentException if the stored value is not a double value.
	 */
	DoubleProperty getDoubleProperty(ICacheKey key);

	/**
	 * Read a double value from the cache.
	 * @param key The key of the value.
	 * @param fallback Fallback value if no value could be found for the key.
	 * @return the stored value or the fallback value.
	 */
	double getDoubleValue(ICacheKey key, double fallback);

	/**
	 * Read a double value from the cache.
	 * @param key The key of the value.
	 * @return the stored value or <code>0.0</code>.
	 */
	double getDoubleValue(ICacheKey key);

	/**
	 * Get the integer property for binding purposes.
	 * @param key The key of the value.
	 * @return the property for this key.
	 * @throws java.lang.IllegalArgumentException if the stored value is not a integer value.
	 */
	IntegerProperty getIntegerProperty(ICacheKey key);

	/**
	 * Read a integer value from the cache.
	 * @param key The key of the value.
	 * @return the stored value or <code>0</code>.
	 */
	int getIntegerValue(ICacheKey key);

	/**
	 * Read a integer value from the cache.
	 * @param key The key of the value.
	 * @param fallback Fallback value if no value could be found for the key.
	 * @return the stored value or the fallback value.
	 */
	int getIntegerValue(ICacheKey key, int fallback);

	/**
	 * Get the string property for binding purposes.
	 * @param key The key of the value.
	 * @return the property for this key.
	 * @throws java.lang.IllegalArgumentException if the stored value is not a string value.
	 */
	StringProperty getStringProperty(ICacheKey key);

	/**
	 * Read a string value from the cache.
	 * @param key The key of the value.
	 * @param fallback Fallback value if no value could be found for the key.
	 * @return the stored value or the fallback value.
	 */
	String getStringValue(ICacheKey key, String fallback);

	/**
	 * Read a string value from the cache.
	 * @param key The key of the value.
	 * @return the stored value or <code>null</code>.
	 */
	String getStringValue(ICacheKey key);

	/**
	 * Get the stored value parsed to a string.
	 * @param key The key of the value.
	 * @return the value parsed to a string or <code>null</code>.
	 */
	String getValueAsString(ICacheKey key);

	/**
	 * Try to parse the string value to the type of the key.
	 * @param key The key of the value.
	 * @param value The string value to parse and put into the cache store.
	 * @throws java.lang.IllegalArgumentException if the key or the value is <code>null</code>.
	 * @throws java.lang.NumberFormatException if the value could not be parsed to a double value.
	 */
	void parseAndPutStringValue(ICacheKey key, String value);

	/**
	 * Stores a boolean value in the cache.
	 * @param key The key of the value.
	 * @param value The value to store.
	 * @throws java.lang.IllegalArgumentException if the key does not store a boolean value.
	 */
	void putBooleanValue(ICacheKey key, boolean value);

	/**
	 * Stores a double value in the cache.
	 * @param key The key of the value.
	 * @param value The value to store.
	 * @throws java.lang.IllegalArgumentException if the key does not store a double value.
	 */
	void putDoubleValue(ICacheKey key, double value);

	/**
	 * Stores a integer value in the cache.
	 * @param key The key of the value.
	 * @param value The value to store.
	 * @throws java.lang.IllegalArgumentException if the key does not store a integer value.
	 */
	void putIntegerValue(ICacheKey key, int value);

	/**
	 * Stores a string value in the cache.
	 * @param key The key of the value.
	 * @param value The value to store.
	 * @throws java.lang.IllegalArgumentException if the key does not store a string value.
	 */
	void putStringValue(ICacheKey key, String value);

	/**
	 * Stores a value of type <code>T</code> in the cache.
	 * @param key The key of the value.
	 * @param value The value to store.
	 * @param <T> Type of the stored value.
	 * @throws java.lang.IllegalArgumentException if the type of the value does not matches to the key.
	 */
	<T> void putValue(ICacheKey key, T value);

	/**
	 * Remove the value for this key from the cache.
	 * @param key The key of the value.
	 */
	void removeValue(ICacheKey key);

	/**
	 * Get all the cached values.
	 * @return an unmodifiable map with cached values.
	 */
	Map<ICacheKey, Object> getValues();

	/**
	 * Check if the cache is empty or not.
	 * @return <code>true</code> if the cache is empty, otherwise returns <code>false</code>.
	 */
	boolean isEmpty();
}
