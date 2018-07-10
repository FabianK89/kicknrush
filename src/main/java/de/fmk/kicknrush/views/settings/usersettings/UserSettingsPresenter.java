package de.fmk.kicknrush.views.settings.usersettings;

import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.UserCacheKey;
import de.fmk.kicknrush.models.Status;
import de.fmk.kicknrush.models.settings.UserSettingsModel;
import de.fmk.kicknrush.views.settings.ISettingsPresenter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.controlsfx.control.NotificationPane;

import javax.inject.Inject;
import javax.inject.Named;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ResourceBundle;

public class UserSettingsPresenter implements ISettingsPresenter, Initializable {
	private final BooleanProperty correctOldPwd;
	private final BooleanProperty equalNewPwd;
	private final BooleanProperty newPassword;
	private final BooleanProperty uniqueUsername;

	@FXML
	private Button        saveButton;
	@FXML
	private PasswordField newPasswordInput;
	@FXML
	private PasswordField oldPasswordInput;
	@FXML
	private PasswordField repeatPasswordInput;
	@FXML
	private TextField     usernameInput;

	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider     cacheProvider;
	@Inject
	private UserSettingsModel model;

	private NotificationPane notificationPane;
	private ResourceBundle   resources;


	public UserSettingsPresenter() {
		correctOldPwd  = new SimpleBooleanProperty(false);
		equalNewPwd    = new SimpleBooleanProperty(false);
		newPassword    = new SimpleBooleanProperty(false);
		uniqueUsername = new SimpleBooleanProperty(false);
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = resources;

		saveButton.disableProperty().bind(uniqueUsername.not().or(equalNewPwd.not()).or(newPassword.not()));

		model.statusProperty().addListener((observable, oldStatus, newStatus) -> {
			if (Status.SUCCESS == newStatus) {
				notificationPane.show(resources.getString("msg.save"));
				oldPasswordInput.clear();
				newPasswordInput.clear();
				repeatPasswordInput.clear();
			}
		});

		usernameInput.textProperty().addListener((observable, oldName, newName) -> {
			if (newName == null || newName.isEmpty())
				uniqueUsername.set(false);

			uniqueUsername.set(model.isUniqueName(newName));
		});
		oldPasswordInput.textProperty().addListener((observable, oldPwd, newPwd) ->
				newPassword.set(!model.passwordsAreEqual(newPwd, newPasswordInput.getText())));
		newPasswordInput.textProperty().addListener((observable, oldPwd, newPwd) -> {
				equalNewPwd.set(model.passwordsAreEqual(newPwd, repeatPasswordInput.getText()));
				newPassword.set(!model.passwordsAreEqual(newPwd, oldPasswordInput.getText()));
		});
		repeatPasswordInput.textProperty().addListener((observable, oldPwd, newPwd) ->
				equalNewPwd.set(model.passwordsAreEqual(newPwd, newPasswordInput.getText())));

		usernameInput.setText(cacheProvider.getUserCache().getStringValue(UserCacheKey.USERNAME));
	}


	@FXML
	private void onSave() {
		final String password;
		final String username;

		password = newPasswordInput.getText();
		username = usernameInput.getText();

		if (!model.isOldPasswordCorrect(oldPasswordInput.getText())) {
			notificationPane.show(convert(resources.getString("msg.wrong.old.password")));
			return;
		}

		model.save(username, password);
	}


	public String convert(final String text) {
		final byte[] bytes = text.getBytes(Charset.forName("ISO-8859-1"));

		return new String(bytes, Charset.forName("UTF-8"));
	}


	@Override
	public void setNotificationPane(NotificationPane pane) {
		notificationPane = pane;
	}
}
