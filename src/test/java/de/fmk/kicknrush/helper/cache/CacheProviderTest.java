package de.fmk.kicknrush.helper.cache;

import de.fmk.kicknrush.TestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * @author FabianK
 */
public class CacheProviderTest extends TestBase {
	private CacheProvider cacheProvider;


	@Before
	public void setUp() {
		cacheProvider = new CacheProvider();
	}


	@After
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