package de.fmk.kicknrush.models.settings;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.SettingCache;
import de.fmk.kicknrush.helper.cache.SettingCacheKey;
import de.fmk.kicknrush.models.Status;
import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * @author FabianK
 */
@RunWith(MockitoJUnitRunner.class)
class AppSettingsModelTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppSettingsModel.class);

	@Mock
	private CacheProvider   cacheProvider;
	@Mock
	private DatabaseHandler dbHandler;
	@Mock
	private SettingCache    settingCache;

	@InjectMocks
	private AppSettingsModel model;


	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		initializeJavaFX();

		when(cacheProvider.getSettingCache()).thenReturn(settingCache);

		when(settingCache.getValueAsString(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS)).thenReturn("5");
		doNothing().when(settingCache).parseAndPutStringValue(isA(SettingCacheKey.class), isA(String.class));

		doNothing().when(dbHandler).saveSettings(isA(SettingCache.class));
	}


	@AfterEach
	void tearDown() {
		model = null;
	}


	@Test
	void testGetAndSetSetting() {
		assertEquals("5", model.getSetting(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS));
		assertThrows(IllegalArgumentException.class, () -> model.setSetting(null, "9"));

		model.setSetting(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS, "");

		assertEquals("5", model.getSetting(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS));

		model.setSetting(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS, "10");

		assertEquals("10", model.getSetting(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS));
	}


	@Test
	void testSave() throws Exception {
		model.setSetting(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS, "5");
		model.save();

		verify(settingCache).parseAndPutStringValue(isA(SettingCacheKey.class), isA(String.class));
		await().atMost(2, TimeUnit.SECONDS).until(() -> model.statusProperty().get() != Status.RUNNING);
		verify(dbHandler).saveSettings(isA(SettingCache.class));
	}


	private void initializeJavaFX() {
		final CountDownLatch latch;

		latch = new CountDownLatch(1);

		SwingUtilities.invokeLater(() -> {
			new JFXPanel(); // initializes JavaFX environment

			latch.countDown();
		});

		try {
			latch.await(3, TimeUnit.SECONDS);
		}
		catch (InterruptedException iex) {
			LOGGER.warn("Thread has been interrupted in initializeJavaFX.", iex);
		}
	}
}