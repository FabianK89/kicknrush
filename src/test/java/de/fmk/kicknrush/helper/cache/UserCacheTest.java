package de.fmk.kicknrush.helper.cache;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * @author FabianK
 */
@RunWith(JUnitPlatform.class)
public class UserCacheTest {
	private UserCache cache;


	@BeforeEach
	public void setUp() {
		cache = new UserCache();
	}


	@AfterEach
	public void tearDown() {
		cache = null;
	}


	@Test
	public void testBooleanValues() {
		final BooleanProperty property;

		property = new SimpleBooleanProperty();

		assertThrows(IllegalArgumentException.class, () -> cache.getBooleanProperty(UserCacheKey.PASSWORD));
		assertThrows(IllegalArgumentException.class, () -> cache.putBooleanValue(UserCacheKey.PASSWORD, true));

		assertFalse(cache.getBooleanValue(UserCacheKey.IS_ADMIN));
		assertTrue(cache.getBooleanValue(UserCacheKey.CHANGE_PWD, true));

		property.bindBidirectional(cache.getBooleanProperty(UserCacheKey.IS_ADMIN));
		property.set(true);

		cache.putBooleanValue(UserCacheKey.CHANGE_PWD, true);

		assertTrue(cache.getBooleanProperty(UserCacheKey.CHANGE_PWD).get());
		assertTrue(cache.getBooleanValue(UserCacheKey.IS_ADMIN, false));
	}


	@Test
	public void testClear() {
		cache.putStringValue(UserCacheKey.USERNAME, "USER");

		assertEquals("USER", cache.getStringValue(UserCacheKey.USERNAME));

		cache.clear();

		assertNull(cache.getStringValue(UserCacheKey.USERNAME));
	}


	@Test
	public void testParseAndPutStringValue() {
		assertThrows(IllegalArgumentException.class, () -> cache.putValue(null, "Test"));
		assertThrows(IllegalArgumentException.class, () -> cache.putValue(UserCacheKey.IS_ADMIN, null));

		cache.parseAndPutStringValue(UserCacheKey.USERNAME, "USER");
		cache.parseAndPutStringValue(UserCacheKey.CHANGE_PWD, "true");

		assertEquals("USER", cache.getStringValue(UserCacheKey.USERNAME));
		assertTrue(cache.getBooleanValue(UserCacheKey.CHANGE_PWD));
	}


	@Test
	public void testPutValue() {
		assertThrows(IllegalArgumentException.class, () -> cache.putValue(UserCacheKey.IS_ADMIN, "Test"));

		cache.putValue(UserCacheKey.USERNAME, "USER");
		cache.putValue(UserCacheKey.CHANGE_PWD, true);

		assertEquals("USER", cache.getStringValue(UserCacheKey.USERNAME));
		assertTrue(cache.getBooleanValue(UserCacheKey.CHANGE_PWD));
	}


	@Test
	public void testRemoveValue() {
		cache.putStringValue(UserCacheKey.USERNAME, "USER");

		assertEquals("USER", cache.getStringValue(UserCacheKey.USERNAME));

		cache.removeValue(UserCacheKey.USERNAME);

		assertNull(cache.getStringValue(UserCacheKey.USERNAME));
	}


	@Test
	public void testStringValues() {
		final StringProperty property;

		property = new SimpleStringProperty();

		assertThrows(IllegalArgumentException.class, () -> cache.getStringProperty(UserCacheKey.IS_ADMIN));
		assertThrows(IllegalArgumentException.class, () -> cache.putStringValue(UserCacheKey.IS_ADMIN, "Test"));

		assertNull(cache.getStringValue(UserCacheKey.USERNAME));
		assertEquals("ABC", cache.getStringValue(UserCacheKey.PASSWORD, "ABC"));

		property.bindBidirectional(cache.getStringProperty(UserCacheKey.USERNAME));
		property.set("USER");

		cache.putStringValue(UserCacheKey.PASSWORD, "ZYX");

		assertEquals("ZYX", cache.getStringProperty(UserCacheKey.PASSWORD).get());
		assertEquals("USER", cache.getStringValue(UserCacheKey.USERNAME, "Test"));
	}
}