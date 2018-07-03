package de.fmk.kicknrush.helper;

public enum SettingCacheKey {
	LOGIN_AUTOMATIC("login.automatic", Boolean.class),
	LOGIN_WINDOW_HEIGHT("login.window.height", Double.class),
	LOGIN_WINDOW_WIDTH("login.window.width", Double.class),
	WINDOW_HEIGHT("window.height", Double.class),
	WINDOW_MAXIMIZED("window.maximized", Boolean.class),
	WINDOW_WIDTH("window.width", Double.class);


	private Class<?> valueClass;
	private String   key;


	SettingCacheKey(String key, Class<?> valueClass) {
		this.valueClass = valueClass;
		this.key        = key;
	}


	public Class<?> getValueClass() {
		return valueClass;
	}


	public String getKey() {
		return key;
	}


	public static SettingCacheKey getByKey(String key) {
		for (final SettingCacheKey cacheKey : values()) {
			if (cacheKey.getKey().equals(key))
				return cacheKey;
		}

		throw new IllegalArgumentException("No cache key found for the key '" + key + "'.");
	}
}
