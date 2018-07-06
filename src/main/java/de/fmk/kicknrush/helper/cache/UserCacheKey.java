package de.fmk.kicknrush.helper.cache;

/**
 * Keys that are used in the user cache.
 */
public enum UserCacheKey implements ICacheKey {
	CHANGE_PWD("change.password", Boolean.class),
	IS_ADMIN("is.admin", Boolean.class),
	PASSWORD("password", String.class),
	SALT("salt", String.class),
	SESSION("session", String.class),
	USER_ID("user_id", String.class),
	USERNAME("username", String.class);


	private Class<?> valueClass;
	private String   key;


	UserCacheKey(String key, Class<?> valueClass) {
		this.key        = key;
		this.valueClass = valueClass;
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
	 * @return the UserCacheKey for this key identifier.
	 * @throws java.lang.IllegalArgumentException if no key could be found.
	 */
	public static UserCacheKey getByKey(String key) {
		for (final UserCacheKey cacheKey : values())
		{
			if (cacheKey.getKey().equals(key))
				return cacheKey;
		}

		throw new IllegalArgumentException("No cache key found for the key '" + key + "'.");
	}
}
