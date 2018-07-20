package de.fmk.kicknrush.views.login;

import de.fmk.kicknrush.helper.ApplicationHelper;
import de.fmk.kicknrush.helper.UTF8Resources;
import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.SettingCacheKey;
import de.fmk.kicknrush.models.Status;
import de.fmk.kicknrush.models.login.LoginModel;
import de.fmk.kicknrush.views.AppImage;
import de.fmk.kicknrush.views.INotificationPresenter;
import de.fmk.kicknrush.views.Notification;
import de.fmk.kicknrush.views.dashboard.DashboardView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginPresenter implements INotificationPresenter, Initializable {
	@FXML
	private GridPane      grid;
	@FXML
	private PasswordField passwordInput;
	@FXML
	private TextField     usernameInput;

	@Inject
	private ApplicationHelper appHelper;
	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider     cacheProvider;
	@Inject
	private LoginModel        model;

	private Notification  notificationPane;
	private UTF8Resources resources;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = new UTF8Resources(resources);

		model.loadSettings();

		model.statusProperty().addListener((observable, oldStatus, newStatus) -> {
			if (Status.FAILED == newStatus){
				notificationPane.showError(this.resources.get("msg.login.failed"), AppImage.LOGIN_FAILED);
			}
			else if (Status.NO_CONNECTION == newStatus) {
				notificationPane.showError(this.resources.get("msg.server.not.available"), AppImage.CONNECTION_PROBLEM);
			}
			else if (Status.RUNNING == newStatus) {
				notificationPane.showLoading(this.resources.get("msg.loading"));
			}
			else if (Status.SUCCESS == newStatus) {
				cacheProvider.getSettingCache().putDoubleValue(SettingCacheKey.LOGIN_WINDOW_HEIGHT,
				                                               grid.getScene().getWindow().getHeight());
				cacheProvider.getSettingCache().putDoubleValue(SettingCacheKey.LOGIN_WINDOW_WIDTH,
				                                               grid.getScene().getWindow().getWidth());

				appHelper.changeView(new DashboardView().getView(), true);
			}
		});
	}


	@FXML
	private void onLogin() {
		final String password;
		final String username;

		password = passwordInput.getText();
		username = usernameInput.getText();

		if (username == null || username.isEmpty())
		{
			notificationPane.showWarning(resources.get("msg.missing.username"), AppImage.LOGIN_FAILED);
			usernameInput.requestFocus();
			return;
		}

		if (password == null || password.isEmpty())
		{
			notificationPane.showWarning(resources.get("msg.missing.password"), AppImage.LOGIN_FAILED);
			passwordInput.requestFocus();
			return;
		}

		model.login(username, password);
	}


	@FXML
	private void onQuit() {
		Platform.exit();
	}


	@Override
	public void setNotificationPane(Notification pane) {
		notificationPane = pane;
	}


	@Override
	public void enter() {
	}


	@Override
	public boolean leave() {
		return false;
	}
}
