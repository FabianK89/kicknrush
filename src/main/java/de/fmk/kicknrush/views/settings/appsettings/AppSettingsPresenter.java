package de.fmk.kicknrush.views.settings.appsettings;

import de.fmk.kicknrush.helper.cache.SettingCacheKey;
import de.fmk.kicknrush.models.Status;
import de.fmk.kicknrush.models.settings.AppSettingsModel;
import de.fmk.kicknrush.views.Notification;
import de.fmk.kicknrush.views.settings.ISettingsPresenter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.controlsfx.control.NotificationPane;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * @author FabianK
 */
public class AppSettingsPresenter implements ISettingsPresenter, Initializable {
	@FXML
	private TextField hideAfterTimeInput;

	@Inject
	private AppSettingsModel model;

	private Notification notificationPane;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model.statusProperty().addListener((observable, oldStatus, newStatus) -> {
			if (Status.SUCCESS == newStatus)
				notificationPane.show(resources.getString("msg.save"));
			else if (Status.FAILED == newStatus)
				notificationPane.show(resources.getString("msg.save.error"));
		});

		hideAfterTimeInput.textProperty().addListener(((observable, oldValue, newValue) -> {
			if (newValue == null || newValue.isEmpty())
				return;

			if (!newValue.matches("\\d{0,2}"))
				hideAfterTimeInput.setText(oldValue);
		}));
		hideAfterTimeInput.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
			if (isFocused)
				return;

			if (hideAfterTimeInput.getText() == null || hideAfterTimeInput.getText().isEmpty())
			{
				hideAfterTimeInput.setText(model.getSetting(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS));
				return;
			}

			save();
		});
	}


	@Override
	public void enter() {
		hideAfterTimeInput.setText(model.getSetting(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS));
	}


	@Override
	public boolean leave() {
		return true;
	}


	private void save() {
		model.setSetting(SettingCacheKey.NOTIFICATION_HIDE_AFTER_SECONDS, hideAfterTimeInput.getText());

		model.save();
	}


	@Override
	public void setNotificationPane(Notification pane) {
		notificationPane = pane;
	}
}
