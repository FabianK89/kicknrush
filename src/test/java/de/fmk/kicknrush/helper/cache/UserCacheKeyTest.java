package de.fmk.kicknrush.helper.cache;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * @author FabianK
 */
public class UserCacheKeyTest {

	@Test
	public void testGetValueClass() {
		assertEquals(Boolean.class, UserCacheKey.IS_ADMIN.getValueClass());
	}


	@Test
	public void testGetKey() {
		assertEquals("is.admin", UserCacheKey.IS_ADMIN.getKey());
	}


	@Test(expected = IllegalArgumentException.class)
	public void testGetByKey() {
		assertEquals(UserCacheKey.IS_ADMIN, UserCacheKey.getByKey("is.admin"));

		UserCacheKey.getByKey("no.valid.key");
	}
}