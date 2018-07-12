package de.fmk.kicknrush.views.settings;

import de.fmk.kicknrush.views.INotificationPresenter;
import de.fmk.kicknrush.views.settings.appsettings.AppSettingsPresenter;
import de.fmk.kicknrush.views.settings.appsettings.AppSettingsView;
import de.fmk.kicknrush.views.settings.usersettings.UserSettingsPresenter;
import de.fmk.kicknrush.views.settings.usersettings.UserSettingsView;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.controlsfx.control.NotificationPane;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;


public class SettingsPresenter implements Initializable, INotificationPresenter {
	private final ObjectProperty<NotificationPane> notificationPaneProperty;

	@FXML
	private Label appSettingsLabel;
	@FXML
	private Label userSettingsLabel;
	@FXML
	private VBox  settingsBox;

	private Map<Label, ISettingsPresenter> settingsViewMap;


	public SettingsPresenter() {
		notificationPaneProperty = new SimpleObjectProperty<>();
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		settingsViewMap = new HashMap<>();

		addUserSettings();
		addAppSettings();

		notificationPaneProperty.addListener(observable ->
				settingsViewMap.forEach((label, presenter) -> presenter.setNotificationPane(notificationPaneProperty.get())));
	}


	private void addAppSettings() {
		final AppSettingsPresenter presenter;
		final AppSettingsView      view;

		view = new AppSettingsView();

		settingsBox.getChildren().add(view.getView());

		presenter = (AppSettingsPresenter) view.getPresenter();

		settingsViewMap.put(appSettingsLabel, presenter);
	}


	private void addUserSettings() {
		final UserSettingsPresenter presenter;
		final UserSettingsView      view;

		view = new UserSettingsView();

		settingsBox.getChildren().add(view.getView());

		presenter = (UserSettingsPresenter) view.getPresenter();

		settingsViewMap.put(userSettingsLabel, presenter);
	}


	@Override
	public void enter() {
		settingsViewMap.forEach((label, presenter) -> presenter.enter());
	}


	@Override
	public boolean leave() {
		for (ISettingsPresenter presenter : settingsViewMap.values()) {
			if (!presenter.leave())
				return false;
		}

		return true;
	}


	@Override
	public void setNotificationPane(NotificationPane pane) {
		notificationPaneProperty.set(pane);
	}
}
