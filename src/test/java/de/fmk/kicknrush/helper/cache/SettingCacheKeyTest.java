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
public class SettingCacheKeyTest {

	@Test
	public void testGetValueClass() {
		assertEquals(Double.class, SettingCacheKey.WINDOW_HEIGHT.getValueClass());
	}


	@Test
	public void testGetKey() {
		assertEquals("window.height", SettingCacheKey.WINDOW_HEIGHT.getKey());
	}


	@Test
	public void getByKey() {
		assertEquals(SettingCacheKey.WINDOW_HEIGHT, SettingCacheKey.getByKey("window.height"));
		assertThrows(IllegalArgumentException.class, () -> SettingCacheKey.getByKey("no.valid.key"));
	}
}