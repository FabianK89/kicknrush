package de.fmk.kicknrush.views.dashboard;

import com.airhacks.afterburner.views.FXMLView;
import de.fmk.kicknrush.app.App;
import de.fmk.kicknrush.helper.ApplicationHelper;
import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.SettingCacheKey;
import de.fmk.kicknrush.helper.cache.UserCache;
import de.fmk.kicknrush.helper.cache.UserCacheKey;
import de.fmk.kicknrush.models.dashboard.DashboardModel;
import de.fmk.kicknrush.views.INotificationPresenter;
import de.fmk.kicknrush.views.login.LoginView;
import de.fmk.kicknrush.views.settings.SettingsView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URL;
import java.util.ResourceBundle;


public class DashboardPresenter implements Initializable {
	@FXML
	private BorderPane mainPane;
	@FXML
	private Tab        adminTab;
	@FXML
	private Tab        settingsTab;
	@FXML
	private TabPane    tabPane;

	@Inject
	private ApplicationHelper appHelper;
	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider     cacheProvider;
	@Inject
	private DashboardModel    model;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initStageListener();
		initTabs();
	}


	private void initTabs() {
		final UserCache cache;

		cache = cacheProvider.getUserCache();
		cache.getBooleanProperty(UserCacheKey.CHANGE_PWD).addListener((obs, oldValue, newValue) ->
				tabPane.getTabs().stream().filter(tab -> tab != settingsTab).forEach(tab -> tab.setDisable(newValue)));

		if (!cache.getBooleanValue(UserCacheKey.IS_ADMIN))
			tabPane.getTabs().remove(adminTab);

		_createTabView(settingsTab, new SettingsView());

		if (cache.getBooleanValue(UserCacheKey.CHANGE_PWD))
			tabPane.getSelectionModel().select(settingsTab);

		tabPane.getTabs().stream()
		       .filter(tab -> tab != settingsTab)
		       .forEach(tab -> tab.setDisable(cache.getBooleanValue(UserCacheKey.CHANGE_PWD)));
	}


	private void _createTabView(final Tab tab, final FXMLView view) {
		final INotificationPresenter presenter;
		final NotificationPane       notificationPane;
		final Parent                 pane;

		pane = view.getView();

		if (view.getPresenter() instanceof INotificationPresenter)
			presenter = (INotificationPresenter) view.getPresenter();
		else
			presenter = null;

		if (presenter == null) {
			tab.setContent(pane);
			return;
		}

		notificationPane = new NotificationPane(pane);
		notificationPane.setShowFromTop(false);
		notificationPane.setCloseButtonVisible(false);
		notificationPane.setOnShown(event -> {
			if (notificationPane.isShowing())
				hideNotificationAfter5Seconds(notificationPane);
		});

		presenter.setNotificationPane(notificationPane);
		tab.setContent(notificationPane);
	}


	private void hideNotificationAfter5Seconds(final NotificationPane pane) {
		new Thread(() -> {
			try {
				Thread.sleep(5000);
			}
			catch (InterruptedException iex) {
				Thread.currentThread().interrupt();
			}

			Platform.runLater(pane::hide);
		}).start();
	}


	private void initStageListener() {
		final Stage primaryStage;

		primaryStage = App.getPrimaryStage();
		primaryStage.setOnCloseRequest(event -> {
			if (!model.logout())
				event.consume();
		});
		primaryStage.widthProperty().addListener((observable, oldWidth, newWidth) ->
				cacheProvider.getSettingCache().putDoubleValue(SettingCacheKey.WINDOW_WIDTH, newWidth.doubleValue()));
		primaryStage.heightProperty().addListener((observable, oldHeight, newHeight) ->
				cacheProvider.getSettingCache().putDoubleValue(SettingCacheKey.WINDOW_HEIGHT, newHeight.doubleValue()));
		primaryStage.maximizedProperty().addListener((observable, wasMaximized, isMaximized) ->
				cacheProvider.getSettingCache().putBooleanValue(SettingCacheKey.WINDOW_MAXIMIZED, isMaximized));
	}


	@FXML
	private void onLogout() {
		if (model.logout())
			appHelper.changeView(new LoginView().getView(), false);
	}


	@FXML
	private void onQuit() {
		if (model.logout())
			Platform.exit();
	}
}
