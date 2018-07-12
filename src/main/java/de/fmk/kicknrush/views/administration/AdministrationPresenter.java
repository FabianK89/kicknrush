package de.fmk.kicknrush.views.administration;

import de.fmk.kicknrush.helper.UTF8Resources;
import de.fmk.kicknrush.models.administration.AdministrationModel;
import de.fmk.kicknrush.models.pojo.User;
import de.fmk.kicknrush.views.settings.ISettingsPresenter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.NotificationPane;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class AdministrationPresenter implements ISettingsPresenter, Initializable {
	@FXML
	private Button         cancelButton;
	@FXML
	private Button         createButton;
	@FXML
	private Button         deleteButton;
	@FXML
	private Button         saveButton;
	@FXML
	private GridPane       detailPane;
	@FXML
	private ListView<User> userList;

	@Inject
	private AdministrationModel model;

	private NotificationPane notificationPane;
	private UTF8Resources    resources;


	@FXML
	private void onCancel() {

	}


	@FXML
	private void onCreate() {

	}


	@FXML
	private void onDelete() {

	}


	@FXML
	private void onSave() {

	}


	@Override
	public void enter() {

	}


	@Override
	public boolean leave() {
		return true;
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = new UTF8Resources(resources);

		initButtons();
		initList();

		detailPane.visibleProperty().bind(userList.getSelectionModel().selectedItemProperty().isNotNull());
	}


	private void initButtons()
	{
		cancelButton.disableProperty().bind(detailPane.visibleProperty().not());

		deleteButton.setText(this.resources.get("btn.delete"));
		deleteButton.disableProperty().bind(userList.getSelectionModel().selectedItemProperty().isNull());

		saveButton.disableProperty().bind(detailPane.visibleProperty().not());
	}


	private void initList() {
		userList.setCellFactory(list -> new ListCell<User>() {
			@Override
			protected void updateItem(User item, boolean empty) {
				super.updateItem(item, empty);

				if (item == null || empty)
					setText(null);
				else
					setText(item.getUsername());
			}
		});

		userList.setItems(model.getUsers());
	}


	@Override
	public void setNotificationPane(NotificationPane pane) {
		notificationPane = pane;
	}
}
