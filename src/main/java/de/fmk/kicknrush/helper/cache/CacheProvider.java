package de.fmk.kicknrush.helper.cache;

import javax.annotation.PostConstruct;


/**
 * CacheProvider for important values of the application.
 *
 * @author FabianK
 */
public class CacheProvider {
	public static final String CACHE_ID = "kicknrush.cache";

	private SettingCache settingCache;
	private UserCache    userCache;


	/**
	 * Default constructor.
	 */
	public CacheProvider() {
		init();
	}


	@PostConstruct
	public void init() {
		settingCache = new SettingCache();
		userCache    = new UserCache();
	}


	/**
	 * Clear the setting cache.
	 */
	public void clearSettingCache() {
		settingCache.clear();
	}


	/**
	 * Clear the user cache.
	 */
	public void clearUserCache() {
		userCache.clear();
	}


	/**
	 * @return the cache for setting values.
	 */
	public SettingCache getSettingCache() {
		return settingCache;
	}


	/**
	 * @return the cache for user values.
	 */
	public UserCache getUserCache() {
		return userCache;
	}
}
