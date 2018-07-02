package de.fmk.kicknrush.helper;

import javafx.scene.image.Image;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


public class ResourceHelperTest {
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
	public void testGetAppIcons() {
		final List<Image> appIcons;

		appIcons = ResourceHelper.getAppIcons();

		assertFalse(appIcons.isEmpty());
		assertEquals(3, appIcons.size());
	}
}