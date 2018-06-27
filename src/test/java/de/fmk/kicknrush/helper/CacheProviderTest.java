package de.fmk.kicknrush.helper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class CacheProviderTest
{
	private static final double DELTA = 0.0000001;
	private static final String VALUE = "Value";

	private CacheProvider cacheProvider;


	@Before
	public void setUp() {
		cacheProvider = new CacheProvider();
		cacheProvider.init();
	}


	@After
	public void tearDown() {
		cacheProvider = null;
	}


	@Test
	public void testClearAllCaches() {
		cacheProvider.putSetting(SettingCacheKey.WINDOW_HEIGHT, VALUE);
		cacheProvider.putUserValue(UserCacheKey.USERNAME, VALUE);

		assertEquals(VALUE, cacheProvider.getSetting(SettingCacheKey.WINDOW_HEIGHT));
		assertEquals(VALUE, cacheProvider.getUserValue(UserCacheKey.USERNAME));

		cacheProvider.clearAllCaches();

		assertNull(cacheProvider.getSetting(SettingCacheKey.WINDOW_HEIGHT));
		assertNull(cacheProvider.getUserValue(UserCacheKey.USERNAME));
	}


	@Test
	public void testClearSettingsCache() {
		cacheProvider.putSetting(SettingCacheKey.WINDOW_HEIGHT, VALUE);

		assertEquals(VALUE, cacheProvider.getSetting(SettingCacheKey.WINDOW_HEIGHT));

		cacheProvider.clearSettingsCache();

		assertNull(cacheProvider.getSetting(SettingCacheKey.WINDOW_HEIGHT));
	}


	@Test
	public void testClearUserCache() {
		cacheProvider.putUserValue(UserCacheKey.USERNAME, VALUE);

		assertEquals(VALUE, cacheProvider.getUserValue(UserCacheKey.USERNAME));

		cacheProvider.clearUserCache();

		assertNull(cacheProvider.getUserValue(UserCacheKey.USERNAME));
	}


	@Test
	public void testGetSettingsCache() {
		final Map<SettingCacheKey, String> cache;

		cacheProvider.putSetting(SettingCacheKey.WINDOW_HEIGHT, VALUE);

		cache = cacheProvider.getSettingsCache();

		assertEquals(1, cache.size());
		assertEquals(VALUE, cache.get(SettingCacheKey.WINDOW_HEIGHT));
	}


	@Test
	public void testPutAndGetSetting() {
		assertNull(cacheProvider.getSetting(SettingCacheKey.WINDOW_HEIGHT));

		cacheProvider.putSetting(SettingCacheKey.WINDOW_HEIGHT, VALUE);
		cacheProvider.putSetting(SettingCacheKey.WINDOW_WIDTH, "800.0");
		cacheProvider.putSetting(SettingCacheKey.WINDOW_MAXIMIZED, Boolean.TRUE.toString());

		assertNull(cacheProvider.getSetting(SettingCacheKey.LOGIN_AUTOMATIC));
		assertEquals(VALUE, cacheProvider.getSetting(SettingCacheKey.WINDOW_HEIGHT));
		assertEquals(600.0, cacheProvider.getDoubleSetting(SettingCacheKey.WINDOW_HEIGHT, 600.0), DELTA);
		assertEquals(600.0, cacheProvider.getDoubleSetting(null, 600.0), DELTA);
		assertEquals(800.0, cacheProvider.getDoubleSetting(SettingCacheKey.WINDOW_WIDTH, 600.0), DELTA);
		assertFalse(cacheProvider.getBooleanSetting(null, false));
		assertTrue(cacheProvider.getBooleanSetting(SettingCacheKey.WINDOW_MAXIMIZED, false));
	}


	@Test
	public void testPutAndGetUserValue()
	{
		assertNull(cacheProvider.getUserValue(UserCacheKey.USERNAME));

		cacheProvider.putUserValue(UserCacheKey.USERNAME, VALUE);
		cacheProvider.putUserValue(UserCacheKey.IS_ADMIN, Boolean.TRUE.toString());

		assertNull(cacheProvider.getUserValue(UserCacheKey.PASSWORD));
		assertEquals(VALUE, cacheProvider.getUserValue(UserCacheKey.USERNAME));
		assertFalse(cacheProvider.getBooleanUserValue(null, false));
		assertTrue(cacheProvider.getBooleanUserValue(UserCacheKey.IS_ADMIN, false));
	}


	@Test
	public void testRemoveSetting() {
		cacheProvider.putSetting(SettingCacheKey.WINDOW_HEIGHT, VALUE);

		assertEquals(VALUE, cacheProvider.getSetting(SettingCacheKey.WINDOW_HEIGHT));

		cacheProvider.removeSetting(SettingCacheKey.WINDOW_HEIGHT);

		assertNull(cacheProvider.getSetting(SettingCacheKey.WINDOW_HEIGHT));
	}


	@Test
	public void testRemoveUserValue() {
		cacheProvider.putUserValue(UserCacheKey.USERNAME, VALUE);

		assertEquals(VALUE, cacheProvider.getUserValue(UserCacheKey.USERNAME));

		cacheProvider.removeUserValue(UserCacheKey.USERNAME);

		assertNull(cacheProvider.getUserValue(UserCacheKey.USERNAME));
	}
}