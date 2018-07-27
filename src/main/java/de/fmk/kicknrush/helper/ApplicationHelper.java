package de.fmk.kicknrush.helper;


import de.fmk.kicknrush.app.App;
import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.SettingCache;
import de.fmk.kicknrush.helper.cache.SettingCacheKey;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * Helper class for the application.
 *
 * @author FabianK
 */
public class ApplicationHelper {
	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider cacheProvider;


	/**
	 * Change the view of the application to the given root pane.
	 * @param root The root pane of the new view.
	 * @param resizable <code>true</code>, if the stage should be resizable, otherwise <code>false</code>.
	 */
	public void changeView(final Parent root, final boolean resizable) {
		final Pane         pane;
		final SettingCache cache;
		final Stage        primaryStage;
		final String       id;

		if (!(root instanceof Pane))
			throw new IllegalArgumentException("The parameter 'root' must be an instance of Pane.");

		cache        = cacheProvider.getSettingCache();
		primaryStage = App.getPrimaryStage();
		pane         = (Pane) root;
		id           = pane.getId();

		primaryStage.setScene(new Scene(root));
		primaryStage.getScene().getStylesheets().add(App.class.getResource("notificationpane.css").toExternalForm());

		if ("mainPane".equals(id)) {
			primaryStage.setHeight(cache.getDoubleValue(SettingCacheKey.WINDOW_HEIGHT, pane.getHeight()));
			primaryStage.setWidth(cache.getDoubleValue(SettingCacheKey.WINDOW_WIDTH, pane.getWidth()));
			primaryStage.setMinHeight(600.0);
			primaryStage.setMinWidth(800.0);
			primaryStage.setMaximized(cache.getBooleanValue(SettingCacheKey.WINDOW_MAXIMIZED));
		}
		else {
			primaryStage.setHeight(cache.getDoubleValue(SettingCacheKey.LOGIN_WINDOW_HEIGHT, 350.0));
			primaryStage.setWidth(cache.getDoubleValue(SettingCacheKey.LOGIN_WINDOW_WIDTH, 350.0));
			primaryStage.setMinHeight(cache.getDoubleValue(SettingCacheKey.LOGIN_WINDOW_HEIGHT, 350.0));
			primaryStage.setMinWidth(cache.getDoubleValue(SettingCacheKey.LOGIN_WINDOW_WIDTH, 350.0));
			primaryStage.setMaximized(false);
		}

		primaryStage.centerOnScreen();
		primaryStage.setResizable(resizable);
	}


	/**
	 * Fill the settings cache with initial data.
	 */
	public void fillSettingsCache() throws IOException {
		final Properties properties;

		properties = new Properties();

		try (InputStream is = getClass().getResourceAsStream("initialSettings.properties")) {
			properties.load(is);
			properties.forEach((key, value) -> {
				final SettingCacheKey cacheKey;

				if (!(key instanceof String) || !(value instanceof String))
					return;

				cacheKey = SettingCacheKey.getByKey((String) key);

				cacheProvider.getSettingCache().parseAndPutStringValue(cacheKey, (String) value);
			});
		}
	}
}
