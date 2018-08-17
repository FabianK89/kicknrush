package de.fmk.kicknrush.db;

import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.ResourceHelper;
import de.fmk.kicknrush.helper.cache.SettingCacheKey;
import de.fmk.kicknrush.models.pojo.User;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


public class DatabaseHandlerTest {
	private static final String SALT      = "hQLrtoWGqCjqepzZZuqv";
	private static final String TEST_USER = "TestUser";
	private static final String UUID      = "c94cc529-b25d-4dc2-95f7-213bc743b201";

	private static Path dbPath;

	private DatabaseHandler dbHandler;
	private Properties      properties;


	@BeforeClass
	public static void setUp() throws Exception {
		final URL testDbUrl;

		dbPath     = Paths.get("test", "db.mv.db").toAbsolutePath();
		testDbUrl  = DatabaseHandlerTest.class.getResource("db.mv.db");

		Files.createDirectories(dbPath.getParent());
		Files.copy(Paths.get(testDbUrl.toURI()), dbPath);
	}


	@Before
	public void setUpHandler() {
		properties = ResourceHelper.loadProperties(DatabaseHandlerTest.class, "db.properties");
		dbHandler  = new DatabaseHandler(properties);
	}


	@After
	public void tearDownHandler() throws Exception {
		dbHandler.closeConnections();

		dbHandler  = null;
		properties = null;
	}


	@AfterClass
	public static void tearDown() throws Exception {
		Files.deleteIfExists(dbPath);
		Files.deleteIfExists(dbPath.resolveSibling("db2.mv.db"));
		Files.deleteIfExists(dbPath.resolveSibling("db2.trace.db"));
		Files.deleteIfExists(dbPath.resolveSibling("db.trace.db"));
		Files.deleteIfExists(dbPath.getParent());

		dbPath = null;
	}


	@Test
	public void testCreateInitialTables() throws Exception {
		final DatabaseHandler initHandler;
		final List<String>    tables;

		properties.setProperty("db.url", "jdbc:h2:./test/db2");

		assertFalse(Files.exists(dbPath.resolveSibling("db2.mv.db")));

		initHandler = new DatabaseHandler(properties);
		initHandler.createInitialTables();

		assertTrue(Files.exists(dbPath.resolveSibling("db2.mv.db")));

		tables = initHandler.getExistingTables();

		assertTrue(tables.contains(DBConstants.TBL_NAME_SETTINGS));
		assertTrue(tables.contains(DBConstants.TBL_NAME_USER));

		initHandler.closeConnections();
	}


	@Test
	public void testLoadAndSaveSettings() throws Exception {
		final CacheProvider cache;

		cache = new CacheProvider();
		cache.init();

		dbHandler.loadSettings(cache);

		assertFalse(cache.getSettingCache().getBooleanValue(SettingCacheKey.WINDOW_MAXIMIZED, true));
		assertEquals(600.0, cache.getSettingCache().getDoubleValue(SettingCacheKey.WINDOW_WIDTH, 200.0), 0.0000001);
		assertEquals(800.0, cache.getSettingCache().getDoubleValue(SettingCacheKey.WINDOW_HEIGHT, 800.0), 0.0000001);

		cache.getSettingCache().putDoubleValue(SettingCacheKey.WINDOW_HEIGHT, 500.0);
		cache.getSettingCache().putDoubleValue(SettingCacheKey.WINDOW_WIDTH, 500.0);

		dbHandler.saveSettings(cache.getSettingCache());

		cache.clearSettingCache();

		assertTrue(cache.getSettingCache().getBooleanValue(SettingCacheKey.WINDOW_MAXIMIZED, true));
		assertEquals(200.0, cache.getSettingCache().getDoubleValue(SettingCacheKey.WINDOW_WIDTH, 200.0), 0.0000001);
		assertEquals(800.0, cache.getSettingCache().getDoubleValue(SettingCacheKey.WINDOW_HEIGHT, 800.0), 0.0000001);

		dbHandler.loadSettings(cache);

		assertFalse(cache.getSettingCache().getBooleanValue(SettingCacheKey.WINDOW_MAXIMIZED, true));
		assertEquals(500.0, cache.getSettingCache().getDoubleValue(SettingCacheKey.WINDOW_WIDTH, 200.0), 0.0000001);
		assertEquals(500.0, cache.getSettingCache().getDoubleValue(SettingCacheKey.WINDOW_HEIGHT, 800.0), 0.0000001);
	}


	@Test
	public void testLoginUser() throws Exception {
		final User user;

		assertNull(dbHandler.loginUser(null, "Test"));
		assertNull(dbHandler.loginUser(TEST_USER, null));

		user = dbHandler.loginUser(TEST_USER, "123456");

		assertNotNull(user);
		assertEquals(UUID, user.getId());
		assertEquals(TEST_USER, user.getUsername());
		assertEquals("123456", user.getPassword());
		assertFalse(user.isAdmin());
		assertNull(user.getSalt());
	}


	@Test
	public void testReadSalt() throws Exception {
		try {
			dbHandler.readSalt(null);
			fail("An IllegalArgumentException must occur.");
		}
		catch (Exception ex) {
			assertTrue(ex instanceof IllegalArgumentException);
		}

		assertEquals(SALT, dbHandler.readSalt(TEST_USER));
	}


	@Test
	public void testUpdateUser() throws Exception {
		final String uuid;
		final User   newUser;
		final User   updateUser;

		try {
			dbHandler.updateUser(null);
			fail("An IllegalArgumentException must occur.");
		}
		catch (Exception ex) {
			assertTrue(ex instanceof IllegalArgumentException);
		}

		assertNotNull(dbHandler.loginUser(TEST_USER, "123456"));
		assertNull(dbHandler.loginUser("NewUser", "abcdef"));
		assertNull(dbHandler.loginUser("UpdatedUser", "654321"));
//
//		uuid    = java.util.UUID.randomUUID().toString();
//		newUser = new User(false, uuid, "abcdef", "newSalt", "NewUser", null);
//
//		dbHandler.updateUser(newUser);
//
//		assertNotNull(dbHandler.loginUser(TEST_USER, "123456"));
//		assertNotNull(dbHandler.loginUser("NewUser", "abcdef"));
//		assertNull(dbHandler.loginUser("UpdatedUser", "654321"));
//
//		updateUser = new User(uuid, true, "654321", SALT, "UpdatedUser", java.util.UUID.randomUUID().toString());
//
//		dbHandler.updateUser(updateUser);
//
//		assertNotNull(dbHandler.loginUser(TEST_USER, "123456"));
//		assertNull(dbHandler.loginUser("NewUser", "abcdef"));
//		assertNotNull(dbHandler.loginUser("UpdatedUser", "654321"));

	}
}