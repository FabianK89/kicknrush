package de.fmk.kicknrush.helper.cache;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author FabianK
 */
@RunWith(JUnitPlatform.class)
public class CacheProviderTest {
	private CacheProvider cacheProvider;


	@BeforeEach
	public void setUp() {
		cacheProvider = new CacheProvider();
	}


	@AfterEach
	public void tearDown() {
		cacheProvider = null;
	}


	@Test
	public void testGetSettingCache() {
		assertNotNull(cacheProvider.getSettingCache());
	}


	@Test
	public void testGetUserCache() {
		assertNotNull(cacheProvider.getUserCache());
	}
}