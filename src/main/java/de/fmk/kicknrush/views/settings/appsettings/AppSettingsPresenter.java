package de.fmk.kicknrush.views.settings.appsettings;

import de.fmk.kicknrush.models.settings.AppSettingsModel;
import de.fmk.kicknrush.views.settings.ISettingsPresenter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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
	private Button    saveButton;
	@FXML
	private TextField hideAfterTimeInput;

	@Inject
	private AppSettingsModel model;

	private NotificationPane notificationPane;


	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}


	@FXML
	private void onSave() {

	}


	@Override
	public void setNotificationPane(NotificationPane pane) {
		notificationPane = pane;
	}
}
