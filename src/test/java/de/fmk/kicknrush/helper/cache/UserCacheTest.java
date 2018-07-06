package de.fmk.kicknrush.helper.cache;

import de.fmk.kicknrush.TestBase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


/**
 * @author FabianK
 */
public class UserCacheTest extends TestBase {
	private UserCache cache;


	@Before
	public void setUp() {
		cache = new UserCache();
	}


	@After
	public void tearDown() {
		cache = null;
	}


	@Test
	public void testBooleanValues() {
		final BooleanProperty property;

		property = new SimpleBooleanProperty();

		throwsIllegalArgumentException(UserCacheKey.PASSWORD, key -> cache.getBooleanProperty(key));
		throwsIllegalArgumentException(UserCacheKey.PASSWORD, key -> cache.putBooleanValue(key, true));

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
		throwsIllegalArgumentException(null, key -> cache.putValue(null, "Test"));
		throwsIllegalArgumentException(UserCacheKey.IS_ADMIN, key -> cache.putValue(key, null));

		cache.parseAndPutStringValue(UserCacheKey.USERNAME, "USER");
		cache.parseAndPutStringValue(UserCacheKey.CHANGE_PWD, "true");

		assertEquals("USER", cache.getStringValue(UserCacheKey.USERNAME));
		assertTrue(cache.getBooleanValue(UserCacheKey.CHANGE_PWD));
	}


	@Test
	public void testPutValue() {
		throwsIllegalArgumentException(UserCacheKey.IS_ADMIN, key -> cache.putValue(key, "Test"));

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

		throwsIllegalArgumentException(UserCacheKey.IS_ADMIN, key -> cache.getStringProperty(key));
		throwsIllegalArgumentException(UserCacheKey.IS_ADMIN, key -> cache.putStringValue(key, "Test"));

		assertNull(cache.getStringValue(UserCacheKey.USERNAME));
		assertEquals("ABC", cache.getStringValue(UserCacheKey.PASSWORD, "ABC"));

		property.bindBidirectional(cache.getStringProperty(UserCacheKey.USERNAME));
		property.set("USER");

		cache.putStringValue(UserCacheKey.PASSWORD, "ZYX");

		assertEquals("ZYX", cache.getStringProperty(UserCacheKey.PASSWORD).get());
		assertEquals("USER", cache.getStringValue(UserCacheKey.USERNAME, "Test"));
	}
}