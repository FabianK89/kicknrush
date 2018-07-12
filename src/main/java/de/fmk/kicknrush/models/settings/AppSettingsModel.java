package de.fmk.kicknrush.models.settings;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.SettingCache;
import de.fmk.kicknrush.helper.cache.SettingCacheKey;
import de.fmk.kicknrush.models.AbstractStatusModel;
import de.fmk.kicknrush.models.Status;
import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


/**
 * Model of the {@link de.fmk.kicknrush.views.settings.appsettings.AppSettingsPresenter}.
 *
 * @author FabianK
 */
public class AppSettingsModel extends AbstractStatusModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(AppSettingsModel.class);

	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider   cacheProvider;
	@Inject
	private DatabaseHandler dbHandler;

	private Map<SettingCacheKey, String> settings;


	/**
	 * Default constructor.
	 */
	public AppSettingsModel() {
		super();
		init();
	}


	/**
	 * Get a setting value from the stored values. If the store is empty, build up the store from the cache.
	 * @param key Key of a setting.
	 * @return the setting value to the given key.
	 */
	public String getSetting(SettingCacheKey key) {
		if (settings.isEmpty())
			loadSettingsFromCache();

		return settings.get(key);
	}


	/**
	 * Initialize the map.
	 */
	@PostConstruct
	public void init() {
		settings = new HashMap<>();
	}


	/**
	 * Save the settings to the cache and possibly to the database.
	 */
	public void save() {
		final SettingCache cache;

		statusProperty.set(Status.RUNNING);

		cache = cacheProvider.getSettingCache();

		settings.forEach(cache::parseAndPutStringValue);

		new Thread(() -> {
			try {
				dbHandler.saveSettings(cache);
				Platform.runLater(() -> statusProperty.set(Status.SUCCESS));
			}
			catch (SQLException sqlex) {
				LOGGER.error("Could not save the settings to the database.", sqlex);
				Platform.runLater(() -> statusProperty.set(Status.FAILED));
			}
		}).start();
	}


	/**
	 * Change a setting in the store.
	 * @param key The key of the setting.
	 * @param value The new value of the setting.
	 * @throws java.lang.IllegalArgumentException if the key is <code>null</code>.
	 */
	public void setSetting(SettingCacheKey key, String value) {
		if (key == null)
			throw new IllegalArgumentException("The key must not be null.");

		if (value == null || value.isEmpty())
			return;

		settings.put(key, value);
	}


	private void loadSettingsFromCache() {
		final SettingCache cache;

		cache = cacheProvider.getSettingCache();

		settings.put(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS,
		             cache.getValueAsString(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS));
	}
}
