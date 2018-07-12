package de.fmk.kicknrush.helper.cache;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * @author FabianK
 */
@RunWith(JUnitPlatform.class)
class SettingCacheTest {
	private static final double DELTA = 0.0000000001;

	private SettingCache cache;


	@BeforeEach
	void setUp() {
		cache = new SettingCache();
	}


	@AfterEach
	void tearDown() {
		cache = null;
	}


	@Test
	void testBooleanValues() {
		final BooleanProperty property;

		property = new SimpleBooleanProperty();

		assertThrows(IllegalArgumentException.class, () -> cache.getBooleanProperty(SettingCacheKey.LOGIN_WINDOW_HEIGHT));
		assertThrows(IllegalArgumentException.class, () -> cache.putBooleanValue(SettingCacheKey.LOGIN_WINDOW_HEIGHT, true));

		assertFalse(cache.getBooleanValue(SettingCacheKey.WINDOW_MAXIMIZED));
		assertTrue(cache.getBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC, true));

		property.bindBidirectional(cache.getBooleanProperty(SettingCacheKey.WINDOW_MAXIMIZED));
		property.set(true);

		cache.putBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC, true);

		assertTrue(cache.getBooleanProperty(SettingCacheKey.LOGIN_AUTOMATIC).get());
		assertTrue(cache.getBooleanValue(SettingCacheKey.WINDOW_MAXIMIZED, false));
	}


	@Test
	void testDoubleValues() {
		final DoubleProperty property;

		property = new SimpleDoubleProperty();

		assertThrows(IllegalArgumentException.class, () -> cache.getDoubleProperty(SettingCacheKey.WINDOW_MAXIMIZED));
		assertThrows(IllegalArgumentException.class, () -> cache.putDoubleValue(SettingCacheKey.WINDOW_MAXIMIZED, 12.5));

		assertEquals(0.0, cache.getDoubleValue(SettingCacheKey.WINDOW_HEIGHT), DELTA);
		assertEquals(555.5, cache.getDoubleValue(SettingCacheKey.WINDOW_WIDTH, 555.5), DELTA);

		property.bindBidirectional(cache.getDoubleProperty(SettingCacheKey.WINDOW_HEIGHT));
		property.set(888.8);

		cache.putDoubleValue(SettingCacheKey.WINDOW_WIDTH, 777.7);

		assertEquals(777.7, cache.getDoubleProperty(SettingCacheKey.WINDOW_WIDTH).get(), DELTA);
		assertEquals(888.8, cache.getDoubleValue(SettingCacheKey.WINDOW_HEIGHT, 555.5), DELTA);
	}


	@Test
	void testForEach() {
		assertThrows(NullPointerException.class, () -> cache.forEachKey(null));
		cache.putBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC, true);
		cache.putDoubleValue(SettingCacheKey.WINDOW_HEIGHT, 666.6);
		cache.forEachKey(key -> assertTrue(key == SettingCacheKey.LOGIN_AUTOMATIC || key == SettingCacheKey.WINDOW_HEIGHT));
	}


	@Test
	void testGetValueAsString() {
		cache.putBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC, true);
		cache.putDoubleValue(SettingCacheKey.WINDOW_HEIGHT, 666.6);
		cache.putIntegerValue(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS, 12);

		assertNull(cache.getValueAsString(null));
		assertEquals(Boolean.TRUE.toString(), cache.getValueAsString(SettingCacheKey.LOGIN_AUTOMATIC));
		assertEquals(Double.toString(666.6), cache.getValueAsString(SettingCacheKey.WINDOW_HEIGHT));
		assertEquals(Integer.toString(12), cache.getValueAsString(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS));
	}


	@Test
	void testGetValues() {
		final Map<ICacheKey, Object> map;

		cache.putBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC, true);

		map = cache.getValues();

		assertEquals(1, map.size());

		assertThrows(UnsupportedOperationException.class, () -> map.put(SettingCacheKey.WINDOW_HEIGHT, 666.6));
	}


	@Test
	void testIntegerValues() {
		final IntegerProperty property;

		property = new SimpleIntegerProperty();

		assertThrows(IllegalArgumentException.class, () -> cache.getIntegerProperty(SettingCacheKey.WINDOW_MAXIMIZED));
		assertThrows(IllegalArgumentException.class, () -> cache.putIntegerValue(SettingCacheKey.WINDOW_MAXIMIZED, 12));

		assertEquals(1, cache.getIntegerValue(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS, 1));

		property.bindBidirectional(cache.getIntegerProperty(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS));
		property.set(9);

		assertEquals(9, cache.getIntegerProperty(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS).get());

		cache.putIntegerValue(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS, 11);

		assertEquals(11, cache.getIntegerValue(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS));
	}


	@Test
	void testIsEmpty() {
		assertTrue(cache.isEmpty());

		cache.putBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC, true);

		assertFalse(cache.isEmpty());
	}


	@Test
	void testParseAndPutStringValue() {
		assertThrows(IllegalArgumentException.class, () -> cache.parseAndPutStringValue(null, "Test"));
		assertThrows(IllegalArgumentException.class,
		             () -> cache.parseAndPutStringValue(SettingCacheKey.WINDOW_MAXIMIZED, null));

		cache.parseAndPutStringValue(SettingCacheKey.WINDOW_HEIGHT, "666.6");
		cache.parseAndPutStringValue(SettingCacheKey.LOGIN_AUTOMATIC, "true");
		cache.parseAndPutStringValue(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS, "12");

		assertEquals(666.6, cache.getDoubleValue(SettingCacheKey.WINDOW_HEIGHT), DELTA);
		assertTrue(cache.getBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC));
		assertEquals(12, cache.getIntegerValue(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS));
	}


	@Test
	void testPutValue() {
		assertThrows(IllegalArgumentException.class, () -> cache.putValue(SettingCacheKey.WINDOW_MAXIMIZED, "Test"));

		cache.putValue(SettingCacheKey.WINDOW_HEIGHT, 666.6);
		cache.putValue(SettingCacheKey.LOGIN_AUTOMATIC, true);
		cache.putValue(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS, 12);

		assertEquals(666.6, cache.getDoubleValue(SettingCacheKey.WINDOW_HEIGHT), DELTA);
		assertTrue(cache.getBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC));
		assertEquals(12, cache.getIntegerValue(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS));
	}
}