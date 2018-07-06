package de.fmk.kicknrush.views.dashboard;

import de.fmk.kicknrush.app.App;
import de.fmk.kicknrush.helper.ApplicationHelper;
import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.SettingCacheKey;
import de.fmk.kicknrush.helper.cache.UserCacheKey;
import de.fmk.kicknrush.models.dashboard.DashboardModel;
import de.fmk.kicknrush.views.login.LoginView;
import de.fmk.kicknrush.views.settings.SettingsView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

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

		if (!cacheProvider.getUserCache().getBooleanValue(UserCacheKey.IS_ADMIN))
			tabPane.getTabs().remove(adminTab);

		settingsTab.setContent(new SettingsView().getView());

		if (cacheProvider.getUserCache().getBooleanValue(UserCacheKey.CHANGE_PWD))
			tabPane.getSelectionModel().select(settingsTab);
	}


	private void initStageListener()
	{
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
