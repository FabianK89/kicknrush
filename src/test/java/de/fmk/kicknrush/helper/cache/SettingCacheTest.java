package de.fmk.kicknrush.helper.cache;

import de.fmk.kicknrush.TestBase;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


/**
 * @author FabianK
 */
public class SettingCacheTest extends TestBase {
	private static final double DELTA = 0.0000000001;

	private SettingCache cache;


	@Before
	public void setUp() {
		cache = new SettingCache();
	}


	@After
	public void tearDown() {
		cache = null;
	}


	@Test
	public void testBooleanValues() {
		final BooleanProperty property;

		property = new SimpleBooleanProperty();

		throwsIllegalArgumentException(SettingCacheKey.LOGIN_WINDOW_HEIGHT, key -> cache.getBooleanProperty(key));
		throwsIllegalArgumentException(SettingCacheKey.LOGIN_WINDOW_HEIGHT, key -> cache.putBooleanValue(key, true));

		assertFalse(cache.getBooleanValue(SettingCacheKey.WINDOW_MAXIMIZED));
		assertTrue(cache.getBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC, true));

		property.bindBidirectional(cache.getBooleanProperty(SettingCacheKey.WINDOW_MAXIMIZED));
		property.set(true);

		cache.putBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC, true);

		assertTrue(cache.getBooleanProperty(SettingCacheKey.LOGIN_AUTOMATIC).get());
		assertTrue(cache.getBooleanValue(SettingCacheKey.WINDOW_MAXIMIZED, false));
	}


	@Test
	public void testDoubleValues() {
		final DoubleProperty property;

		property = new SimpleDoubleProperty();

		throwsIllegalArgumentException(SettingCacheKey.WINDOW_MAXIMIZED, key -> cache.getDoubleProperty(key));
		throwsIllegalArgumentException(SettingCacheKey.WINDOW_MAXIMIZED, key -> cache.putDoubleValue(key, 12.5));

		assertEquals(0.0, cache.getDoubleValue(SettingCacheKey.WINDOW_HEIGHT), DELTA);
		assertEquals(555.5, cache.getDoubleValue(SettingCacheKey.WINDOW_WIDTH, 555.5), DELTA);

		property.bindBidirectional(cache.getDoubleProperty(SettingCacheKey.WINDOW_HEIGHT));
		property.set(888.8);

		cache.putDoubleValue(SettingCacheKey.WINDOW_WIDTH, 777.7);

		assertEquals(777.7, cache.getDoubleProperty(SettingCacheKey.WINDOW_WIDTH).get(), DELTA);
		assertEquals(888.8, cache.getDoubleValue(SettingCacheKey.WINDOW_HEIGHT, 555.5), DELTA);
	}


	@Test(expected = NullPointerException.class)
	public void testForEach() {
		cache.forEachKey(null);
		cache.putBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC, true);
		cache.putDoubleValue(SettingCacheKey.WINDOW_HEIGHT, 666.6);
		cache.forEachKey(key -> assertTrue(key == SettingCacheKey.LOGIN_AUTOMATIC || key == SettingCacheKey.WINDOW_HEIGHT));
	}


	@Test
	public void testGetValueAsString() {
		cache.putBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC, true);
		cache.putDoubleValue(SettingCacheKey.WINDOW_HEIGHT, 666.6);

		assertNull(cache.getValueAsString(null));
		assertEquals(Boolean.TRUE.toString(), cache.getValueAsString(SettingCacheKey.LOGIN_AUTOMATIC));
		assertEquals(Double.toString(666.6), cache.getValueAsString(SettingCacheKey.WINDOW_HEIGHT));
	}


	@Test(expected = UnsupportedOperationException.class)
	public void testGetValues() {
		final Map<ICacheKey, Object> map;

		cache.putBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC, true);

		map = cache.getValues();

		assertEquals(1, map.size());

		map.put(SettingCacheKey.WINDOW_HEIGHT, 666.6);
	}


	@Test
	public void testIsEmpty() {
		assertTrue(cache.isEmpty());

		cache.putBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC, true);

		assertFalse(cache.isEmpty());
	}


	@Test
	public void testParseAndPutStringValue() {
		throwsIllegalArgumentException(null, key -> cache.parseAndPutStringValue(null, "Test"));
		throwsIllegalArgumentException(SettingCacheKey.WINDOW_MAXIMIZED, key -> cache.parseAndPutStringValue(key, null));

		cache.parseAndPutStringValue(SettingCacheKey.WINDOW_HEIGHT, "666.6");
		cache.parseAndPutStringValue(SettingCacheKey.LOGIN_AUTOMATIC, "true");

		assertEquals(666.6, cache.getDoubleValue(SettingCacheKey.WINDOW_HEIGHT), DELTA);
		assertTrue(cache.getBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC));
	}


	@Test
	public void testPutValue() {
		throwsIllegalArgumentException(SettingCacheKey.WINDOW_MAXIMIZED, key -> cache.putValue(key, "Test"));

		cache.putValue(SettingCacheKey.WINDOW_HEIGHT, 666.6);
		cache.putValue(SettingCacheKey.LOGIN_AUTOMATIC, true);

		assertEquals(666.6, cache.getDoubleValue(SettingCacheKey.WINDOW_HEIGHT), DELTA);
		assertTrue(cache.getBooleanValue(SettingCacheKey.LOGIN_AUTOMATIC));
	}
}