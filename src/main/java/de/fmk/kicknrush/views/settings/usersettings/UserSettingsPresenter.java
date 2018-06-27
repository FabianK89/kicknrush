package de.fmk.kicknrush.views.settings.usersettings;

import de.fmk.kicknrush.views.settings.ISettingsPresenter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UserSettingsPresenter implements ISettingsPresenter, Initializable {
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


	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}


	@FXML
	private void onSave() {

	}
}
