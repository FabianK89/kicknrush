package de.fmk.kicknrush.helper;

public enum UserCacheKey {
	CHANGE_PWD("change.password", Boolean.class),
	IS_ADMIN("is.admin", Boolean.class),
	PASSWORD("password", String.class),
	SALT("salt", String.class),
	USER_ID("user_id", String.class),
	USERNAME("username", String.class);


	private Class<?> valueClass;
	private String   key;


	UserCacheKey(String key, Class<?> valueClass) {
		this.key        = key;
		this.valueClass = valueClass;
	}


	public Class<?> getValueClass() {
		return valueClass;
	}


	public String getKey() {
		return key;
	}


	public static UserCacheKey getByKey(String key) {
		for (final UserCacheKey cacheKey : values())
		{
			if (cacheKey.getKey().equals(key))
				return cacheKey;
		}

		throw new IllegalArgumentException("No cache key found for the key '" + key + "'.");
	}
}
