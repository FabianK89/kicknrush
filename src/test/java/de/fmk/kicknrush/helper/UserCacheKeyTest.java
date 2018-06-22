package de.fmk.kicknrush.helper;

import org.junit.Test;

import static org.junit.Assert.*;


public class UserCacheKeyTest {

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