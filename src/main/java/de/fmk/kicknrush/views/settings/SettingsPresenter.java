package de.fmk.kicknrush.views.settings;

import de.fmk.kicknrush.views.settings.usersettings.UserSettingsPresenter;
import de.fmk.kicknrush.views.settings.usersettings.UserSettingsView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class SettingsPresenter implements Initializable {
	@FXML
	private Label userSettingsLabel;
	@FXML
	private VBox  settingsBox;

	private Map<Label, ISettingsPresenter> settingsViewMap;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		settingsViewMap = new HashMap<>();

		addUserSettings();
	}


	private void addUserSettings() {
		final UserSettingsPresenter presenter;
		final UserSettingsView      view;

		view = new UserSettingsView();

		settingsBox.getChildren().add(view.getView());

		presenter = (UserSettingsPresenter) view.getPresenter();

		settingsViewMap.put(userSettingsLabel, presenter);
	}
}
