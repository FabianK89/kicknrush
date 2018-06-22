package de.fmk.kicknrush.helper;

import org.junit.Test;

import static org.junit.Assert.*;


public class SettingCacheKeyTest {

	@Test
	public void testGetValueClass() {
		assertEquals(Double.class, SettingCacheKey.WINDOW_HEIGHT.getValueClass());
	}


	@Test
	public void testGetKey() {
		assertEquals("window.height", SettingCacheKey.WINDOW_HEIGHT.getKey());
	}


	@Test(expected = IllegalArgumentException.class)
	public void getByKey() {
		assertEquals(SettingCacheKey.WINDOW_HEIGHT, SettingCacheKey.getByKey("window.height"));

		SettingCacheKey.getByKey("no.valid.key");
	}
}