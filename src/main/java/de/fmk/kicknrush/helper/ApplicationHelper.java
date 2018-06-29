package de.fmk.kicknrush.helper;


import de.fmk.kicknrush.app.App;
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
		final Pane   pane;
		final Stage  primaryStage;
		final String id;

		if (!(root instanceof Pane))
			throw new IllegalArgumentException("The parameter 'root' must be an instance of Pane.");

		primaryStage = App.getPrimaryStage();
		pane         = (Pane) root;
		id           = pane.getId();

		primaryStage.setScene(new Scene(root));
		primaryStage.centerOnScreen();
		primaryStage.setResizable(resizable);

		if ("mainPane".equals(id)) {
			primaryStage.setHeight(cacheProvider.getDoubleSetting(SettingCacheKey.WINDOW_HEIGHT, pane.getHeight()));
			primaryStage.setWidth(cacheProvider.getDoubleSetting(SettingCacheKey.WINDOW_WIDTH, pane.getWidth()));
			primaryStage.setMinHeight(600.0);
			primaryStage.setMinWidth(800.0);
			primaryStage.setMaximized(cacheProvider.getBooleanSetting(SettingCacheKey.WINDOW_MAXIMIZED, false));
		}
		else {
			primaryStage.setHeight(pane.getHeight());
			primaryStage.setWidth(pane.getWidth());
			primaryStage.setMinHeight(pane.getHeight());
			primaryStage.setMinWidth(pane.getWidth());
			primaryStage.setMaximized(false);
		}
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

				cacheProvider.putSetting(cacheKey, (String) value);
			});
		}
	}
}
