package de.fmk.kicknrush.helper.cache;

/**
 * Keys that are used in the settings cache.
 */
public enum SettingCacheKey implements ICacheKey {
	LOGIN_AUTOMATIC("login.automatic", Boolean.class),
	LOGIN_WINDOW_HEIGHT("login.window.height", Double.class),
	LOGIN_WINDOW_WIDTH("login.window.width", Double.class),
	NOTIFICATION_HIDE_AFTER_SECONDS("notification.hide.after.seconds", Integer.class),
	WINDOW_HEIGHT("window.height", Double.class),
	WINDOW_MAXIMIZED("window.maximized", Boolean.class),
	WINDOW_WIDTH("window.width", Double.class);


	private Class<?> valueClass;
	private String   key;


	SettingCacheKey(String key, Class<?> valueClass) {
		this.valueClass = valueClass;
		this.key        = key;
	}


	@Override
	public Class<?> getValueClass() {
		return valueClass;
	}


	@Override
	public String getKey() {
		return key;
	}


	/**
	 * @param key The key identifier.
	 * @return the SettingCacheKey for this key identifier.
	 * @throws java.lang.IllegalArgumentException if no key could be found.
	 */
	public static SettingCacheKey getByKey(String key) {
		for (final SettingCacheKey cacheKey : values()) {
			if (cacheKey.getKey().equals(key))
				return cacheKey;
		}

		throw new IllegalArgumentException("No cache key found for the key '" + key + "'.");
	}
}
