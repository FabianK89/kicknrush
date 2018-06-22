package de.fmk.kicknrush.helper;

public enum UserCacheKey {
	IS_ADMIN("is.admin"),
	PASSWORD("password"),
	USER_ID("user_id"),
	USERNAME("username");


	private String key;


	UserCacheKey(String key) {
		this.key = key;
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
