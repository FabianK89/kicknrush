package de.fmk.kicknrush.helper;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * CacheProvider for important values of the application.
 */
public class CacheProvider {
	public static final String CACHE_ID = "kicknrush.cache";

	private Map<SettingCacheKey, String> cachedSettings;
	private Map<UserCacheKey, String>    cachedUserValues;


	/**
	 * Clear all caches.
	 */
	public void clearAllCaches() {
		clearSettingsCache();
		clearUserCache();
	}


	/**
	 * Clear the settings cache.
	 */
	public void clearSettingsCache() {
		if (cachedSettings != null)
			cachedSettings.clear();
	}


	/**
	 * Clear the user cache.
	 */
	public void clearUserCache() {
		if (cachedUserValues != null)
			cachedUserValues.clear();
	}


	/**
	 * @param key The key of a cached setting.
	 * @param fallback Fallback value if no value could be found for the given key.
	 * @return the cached boolean value or the fallback value.
	 */
	public boolean getBooleanSetting(SettingCacheKey key, boolean fallback) {
		final String value;

		if (key == null || key.getValueClass() != Boolean.class || cachedSettings == null || cachedSettings.get(key) == null)
			return fallback;

		value = cachedSettings.get(key);

		return Boolean.parseBoolean(value);
	}


	/**
	 * @param key The key of a cached user value.
	 * @param fallback Fallback value if no value could be found for the given key.
	 * @return the cached boolean value or the fallback value.
	 */
	public boolean getBooleanUserValue(UserCacheKey key, boolean fallback) {
		final String value;

		if (key == null || key.getValueClass() != Boolean.class || cachedUserValues == null || cachedUserValues.get(key) == null)
			return fallback;

		value = cachedUserValues.get(key);

		return Boolean.parseBoolean(value);
	}


	/**
	 * @param key The key of a cached setting.
	 * @param fallback Fallback value if no value could be found for the given key.
	 * @return the double value to the key or the fallback value.
	 */
	public double getDoubleSetting(SettingCacheKey key, double fallback) {
		final String value;

		if (key == null || key.getValueClass() != Double.class || cachedSettings == null || cachedSettings.get(key) == null)
			return fallback;

		value = cachedSettings.get(key);

		try {
			return Double.parseDouble(value);
		}
		catch (NumberFormatException nfex) {
			return fallback;
		}
	}


	/**
	 * @param key The key of a cached setting.
	 * @return the value to the key or <code>null</code>, if not value was found for this key.
	 */
	public String getSetting(SettingCacheKey key) {
		return cachedSettings == null ? null : cachedSettings.get(key);
	}


	/**
	 * @return the unmodifiable state of the settings cache.
	 */
	public Map<SettingCacheKey, String> getSettingsCache() {
		return Collections.unmodifiableMap(cachedSettings);
	}


	/**
	 * @param key The key of an cached user value.
	 * @return the value to the key or <code>null</code>, if no value was found for this key.
	 */
	public String getUserValue(UserCacheKey key) {
		return cachedUserValues == null ? null : cachedUserValues.get(key);
	}


	@PostConstruct
	public void init() {
		cachedSettings   = new HashMap<>();
		cachedUserValues = new HashMap<>();
	}


	/**
	 * Put a key-value-pair to the settings cache.
	 * @param key Key of the setting.
	 * @param value Value to store in the settings cache.
	 */
	public void putSetting(SettingCacheKey key, String value) {
		if (cachedSettings != null)
			cachedSettings.put(key, value);
	}


	/**
	 * Put a key-value-pair to the user cache.
	 * @param key Key of the user value.
	 * @param value User value to store in the cache.
	 */
	public void putUserValue(UserCacheKey key, String value) {
		if (cachedUserValues != null)
			cachedUserValues.put(key, value);
	}


	/**
	 * Remove the value of the given key from the settings cache.
	 * @param key Key of a value.
	 */
	public void removeSetting(SettingCacheKey key) {
		if (cachedSettings != null)
			cachedSettings.remove(key);
	}


	/**
	 * Remove the value of the given key from the user cache.
	 * @param key Key of a value.
	 */
	public void removeUserValue(UserCacheKey key) {
		if (cachedUserValues != null)
			cachedUserValues.remove(key);
	}
}
