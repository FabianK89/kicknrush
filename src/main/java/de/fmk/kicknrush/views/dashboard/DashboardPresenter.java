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
import de.fmk.kicknrush.views.administration.AdministrationView;
import de.fmk.kicknrush.views.bets.BetsView;
import de.fmk.kicknrush.views.login.LoginView;
import de.fmk.kicknrush.views.settings.SettingsView;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * @author FabianK
 */
public class DashboardPresenter implements Initializable {
	@FXML
	private BorderPane mainPane;
	@FXML
	private Tab        adminTab;
	@FXML
	private Tab        betsTab;
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

	private final BooleanProperty                  disableTabListener;
	private final Map<Tab, INotificationPresenter> tabMap;


	public DashboardPresenter() {
		disableTabListener = new SimpleBooleanProperty(false);
		tabMap             = new HashMap<>();
	}


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

		createTabView(betsTab, new BetsView());

		if (!cache.getBooleanValue(UserCacheKey.IS_ADMIN))
			tabPane.getTabs().remove(adminTab);
		else
			createTabView(adminTab, new AdministrationView());

		createTabView(settingsTab, new SettingsView());

		tabPane.getSelectionModel().selectedItemProperty().addListener((observable, wasSelected, isSelected) -> {
			if (disableTabListener.get()) {
				disableTabListener.set(false);
				return;
			}

			if (wasSelected != null && !tabMap.get(wasSelected).leave()) {
					disableTabListener.set(true);
					Platform.runLater(() -> tabPane.getSelectionModel().select(wasSelected));
					return;
			}

			if (wasSelected != null)
				tabMap.get(wasSelected).leave();

			if (isSelected != null)
				tabMap.get(isSelected).enter();
		});

		if (cache.getBooleanValue(UserCacheKey.CHANGE_PWD)) {
			tabPane.getSelectionModel().select(settingsTab);
		}
		else {
			tabPane.getSelectionModel().clearSelection();
			tabPane.getSelectionModel().select(betsTab);
		}

		tabPane.getTabs().stream()
		       .filter(tab -> tab != settingsTab)
		       .forEach(tab -> tab.setDisable(cache.getBooleanValue(UserCacheKey.CHANGE_PWD)));
	}


	private void createTabView(final Tab tab, final FXMLView view) {
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

		tabMap.put(tab, presenter);

		notificationPane = new NotificationPane(pane);
		notificationPane.setShowFromTop(false);
		notificationPane.setCloseButtonVisible(false);
		notificationPane.setOnShown(event -> {
			if (notificationPane.isShowing())
				hideNotification(notificationPane);
		});

		presenter.setNotificationPane(notificationPane);
		tab.setContent(notificationPane);
	}


	private void hideNotification(final NotificationPane pane) {
		new Thread(() -> {
			final int sleepTime;

			sleepTime = cacheProvider.getSettingCache().getIntegerValue(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS, 5);

			try {
				Thread.sleep(sleepTime * 1000L);
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
