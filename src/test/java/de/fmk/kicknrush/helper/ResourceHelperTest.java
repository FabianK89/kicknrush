package de.fmk.kicknrush.helper;

import javafx.scene.image.Image;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * @author FabianK
 */
@RunWith(JUnitPlatform.class)
public class ResourceHelperTest {
	@Test
	public void testGetAppIcons() {
		final List<Image> appIcons;

		appIcons = ResourceHelper.getAppIcons();

		assertFalse(appIcons.isEmpty());
		assertEquals(3, appIcons.size());
	}


	@Test
	public void testGetResourcePath() throws Exception {
		final String path;

		try {
			ResourceHelper.getResourcePath(null, "test.file");
			fail("An IllegalArgumentException must occur.");
		}
		catch (Exception ex) {
			assertTrue(ex instanceof IllegalArgumentException);
		}

		try {
			ResourceHelper.getResourcePath(getClass(), "test");
			fail("An IllegalArgumentException must occur.");
		}
		catch (Exception ex) {
			assertTrue(ex instanceof IllegalArgumentException);
		}

		path = ResourceHelper.getResourcePath(getClass(), "test.file");

		assertNotNull(path);
		assertNotNull(getClass().getResource(path));
		assertTrue(Files.exists(Paths.get(getClass().getResource(path).toURI())));
	}


	@Test
	public void testLoadProperties() throws Exception {
		final Properties properties;

		try {
			ResourceHelper.loadProperties(null, "test.properties");
			fail("An IllegalArgumentException must occur.");
		}
		catch (Exception ex) {
			assertTrue(ex instanceof IllegalArgumentException);
		}

		try {
			ResourceHelper.loadProperties(getClass(), null);
			fail("An IllegalArgumentException must occur.");
		}
		catch (Exception ex) {
			assertTrue(ex instanceof IllegalArgumentException);
		}

		properties = ResourceHelper.loadProperties(getClass(), "test.properties");

		assertNotNull(properties);
		assertEquals("1", properties.getProperty("test"));
	}
}