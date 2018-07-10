package de.fmk.kicknrush.helper.cache;


import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * @author FabianK
 */
@RunWith(JUnitPlatform.class)
public class UserCacheKeyTest {

	@Test
	public void testGetValueClass() {
		assertEquals(Boolean.class, UserCacheKey.IS_ADMIN.getValueClass());
	}


	@Test
	public void testGetKey() {
		assertEquals("is.admin", UserCacheKey.IS_ADMIN.getKey());
	}


	@Test
	public void testGetByKey() {
		assertEquals(UserCacheKey.IS_ADMIN, UserCacheKey.getByKey("is.admin"));
		assertThrows(IllegalArgumentException.class, () -> UserCacheKey.getByKey("no.valid.key"));
	}
}