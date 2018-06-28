package de.fmk.kicknrush.views.settings.usersettings;

import de.fmk.kicknrush.models.settings.UserSettingsModel;
import de.fmk.kicknrush.views.settings.ISettingsPresenter;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.net.URL;
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

	@Inject
	private UserSettingsModel model;


	public UserSettingsPresenter() {
		correctOldPwd  = new SimpleBooleanProperty(false);
		equalNewPwd    = new SimpleBooleanProperty(false);
		newPassword    = new SimpleBooleanProperty(false);
		uniqueUsername = new SimpleBooleanProperty(false);
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		saveButton.disableProperty()
		          .bind(uniqueUsername.not().or(correctOldPwd.not()).or(equalNewPwd.not()).or(newPassword.not()));

		usernameInput.textProperty().addListener((observable, oldName, newName) -> {
			if (newName == null || newName.isEmpty())
				uniqueUsername.set(false);

			uniqueUsername.set(model.isUniqueName(newName));
		});
		oldPasswordInput.textProperty().addListener((observable, oldPwd, newPwd) -> {
				correctOldPwd.set(model.isOldPasswordCorrect(newPwd));
				newPassword.set(!model.passwordsAreEqual(newPwd, newPasswordInput.getText()));
		});
		newPasswordInput.textProperty().addListener((observable, oldPwd, newPwd) -> {
				equalNewPwd.set(model.passwordsAreEqual(newPwd, repeatPasswordInput.getText()));
				newPassword.set(!model.passwordsAreEqual(newPwd, oldPasswordInput.getText()));
		});
		repeatPasswordInput.textProperty().addListener((observable, oldPwd, newPwd) ->
				equalNewPwd.set(model.passwordsAreEqual(newPwd, newPasswordInput.getText())));
	}


	@FXML
	private void onSave() {
		final String newPassword;
		final String username;

		newPassword = newPasswordInput.getText();
		username    = usernameInput.getText();

		model.save(username, newPassword);
	}
}
