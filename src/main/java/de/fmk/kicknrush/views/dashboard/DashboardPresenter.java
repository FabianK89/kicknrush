package de.fmk.kicknrush.views.dashboard;

import de.fmk.kicknrush.app.App;
import de.fmk.kicknrush.helper.ApplicationHelper;
import de.fmk.kicknrush.helper.CacheProvider;
import de.fmk.kicknrush.helper.SettingCacheKey;
import de.fmk.kicknrush.helper.UserCacheKey;
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

		if (!Boolean.valueOf(cacheProvider.getUserValue(UserCacheKey.IS_ADMIN)))
			tabPane.getTabs().remove(adminTab);

		settingsTab.setContent(new SettingsView().getView());

		if (cacheProvider.getBooleanUserValue(UserCacheKey.CHANGE_PWD, false))
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
				cacheProvider.putSetting(SettingCacheKey.WINDOW_WIDTH, newWidth.toString()));
		primaryStage.heightProperty().addListener((observable, oldHeight, newHeight) ->
				cacheProvider.putSetting(SettingCacheKey.WINDOW_HEIGHT, newHeight.toString()));
		primaryStage.maximizedProperty().addListener((observable, wasMaximized, isMaximized) ->
				cacheProvider.putSetting(SettingCacheKey.WINDOW_MAXIMIZED, isMaximized.toString()));
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
