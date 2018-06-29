package de.fmk.kicknrush.helper;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;


public class ResourceHelperTest {
	@Test
	public void testGetResourcePath() throws Exception {
		final String path;

		path = ResourceHelper.getResourcePath(getClass(), "test.file");

		assertNotNull(path);
		assertNotNull(getClass().getResource(path));
		assertTrue(Files.exists(Paths.get(getClass().getResource(path).toURI())));
	}
}