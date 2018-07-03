package de.fmk.kicknrush.views.login;

import de.fmk.kicknrush.helper.ApplicationHelper;
import de.fmk.kicknrush.helper.CacheProvider;
import de.fmk.kicknrush.helper.SettingCacheKey;
import de.fmk.kicknrush.models.Status;
import de.fmk.kicknrush.models.login.LoginModel;
import de.fmk.kicknrush.views.dashboard.DashboardView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginPresenter implements Initializable {
	@FXML
	private GridPane      grid;
	@FXML
	private PasswordField passwordInput;
	@FXML
	private ProgressBar   progressBar;
	@FXML
	private TextField     usernameInput;
	@FXML
	private TextFlow      textFlow;

	@Inject
	private ApplicationHelper appHelper;
	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider     cacheProvider;
	@Inject
	private LoginModel        model;

	private ResourceBundle bundle;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		bundle = resources;

		progressBar.visibleProperty().bind(model.statusProperty().isEqualTo(Status.RUNNING));

		model.loadSettings();

		model.statusProperty().addListener((observable, oldStatus, newStatus) -> {
			if (Status.FAILED == newStatus){
				showMessage(bundle.getString("msg.login.failed"));
			}
			else if (Status.SUCCESS == newStatus) {
				cacheProvider.putSetting(SettingCacheKey.LOGIN_WINDOW_HEIGHT,
				                         Double.toString(grid.getScene().getWindow().getHeight()));
				cacheProvider.putSetting(SettingCacheKey.LOGIN_WINDOW_WIDTH,
				                         Double.toString(grid.getScene().getWindow().getWidth()));

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
			showMessage(bundle.getString("msg.missing.username"));
			usernameInput.requestFocus();
			return;
		}

		if (password == null || password.isEmpty())
		{
			showMessage(bundle.getString("msg.missing.password"));
			passwordInput.requestFocus();
			return;
		}

		model.login(username, password);
	}


	@FXML
	private void onQuit() {
		Platform.exit();
	}


	private void showMessage(final String message) {
		final Text textNode;

		textNode = new Text(message);
		textNode.setStyle("-fx-fill: white");

		textFlow.getChildren().clear();
		textFlow.getChildren().add(textNode);
	}
}
