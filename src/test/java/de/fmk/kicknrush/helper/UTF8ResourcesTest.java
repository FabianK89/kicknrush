package de.fmk.kicknrush.helper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * @author FabianK
 */
@RunWith(JUnitPlatform.class)
class UTF8ResourcesTest {
	private ResourceBundle bundle;


	@BeforeEach
	void setUp() throws Exception {
		final ClassLoader loader;
		final Path        path;
		final URL[]       urls;

		path   = Paths.get(getClass().getResource("resourceTest.properties").toURI()).getParent();
		urls   = new URL[]{path.toUri().toURL()};
		loader = new URLClassLoader(urls);
		bundle = ResourceBundle.getBundle("resourceTest", Locale.ENGLISH, loader);
	}


	@AfterEach
	void tearDown() {
		bundle = null;
	}


	@Test
	void test() {
		final UTF8Resources utf8 = new UTF8Resources(bundle);

		assertEquals("Äther ist heiß", utf8.get("test"));
		assertEquals("Ã\u0084ther ist heiÃ\u009F", bundle.getString("test"));

		assertNull(utf8.get(null, "test"));
		assertEquals("heißer Äther: würzig, scharf", utf8.get("test.with.arguments", "würzig", "scharf"));
		assertEquals("heißer Äther: %s, %s", utf8.get("test.with.arguments", new String[] {}));
	}
}