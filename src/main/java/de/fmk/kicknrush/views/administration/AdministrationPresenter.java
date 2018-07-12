package de.fmk.kicknrush.views.administration;

import de.fmk.kicknrush.helper.UTF8Resources;
import de.fmk.kicknrush.models.administration.AdministrationModel;
import de.fmk.kicknrush.models.pojo.User;
import de.fmk.kicknrush.views.settings.ISettingsPresenter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.NotificationPane;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class AdministrationPresenter implements ISettingsPresenter, Initializable {
	private final BooleanProperty createdNewUser;
	private final BooleanProperty disableSelectionListener;

	@FXML
	private Button         cancelButton;
	@FXML
	private Button         createButton;
	@FXML
	private Button         deleteButton;
	@FXML
	private Button         saveButton;
	@FXML
	private CheckBox       adminCheck;
	@FXML
	private CheckBox       newPasswordCheck;
	@FXML
	private GridPane       detailPane;
	@FXML
	private ListView<User> userList;
	@FXML
	private TextField      nameInput;

	@Inject
	private AdministrationModel model;

	private NotificationPane notificationPane;
	private UTF8Resources    resources;


	public AdministrationPresenter() {
		createdNewUser           = new SimpleBooleanProperty(false);
		disableSelectionListener = new SimpleBooleanProperty(false);
	}


	@FXML
	private void onCancel() {
		if (createdNewUser.get()) {
			model.removeUser(model.getEditedUser());
			createdNewUser.set(false);
		}

		userList.getSelectionModel().clearSelection();
	}


	@FXML
	private void onCreate() {
		final User user;

		user = model.createUser(resources.get("user.new"));

		fillDetails(user);
		createdNewUser.set(true);
		disableSelectionListener.set(true);
		userList.refresh();
		userList.getSelectionModel().select(user);
	}


	private void fillDetails(final User user) {
		nameInput.setText(user.getUsername());
		adminCheck.setSelected(user.isAdmin());

		nameInput.setDisable(false);
	}


	@FXML
	private void onDelete() {

	}


	@FXML
	private void onSave() {
		final User user;

		if (resources.get("user.new").equals(nameInput.getText()) ||
		    nameInput.getText() == null || nameInput.getText().isEmpty()) {
			notificationPane.show("Gib einen anderen Benutzernamen ein.");
			return;
		}

		user = model.getEditedUser();
		user.setUsername(nameInput.getText());
		user.setAdmin(adminCheck.isSelected());

		createdNewUser.set(false);
		nameInput.setDisable(true);
		userList.refresh();
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
		nameInput.setDisable(true);
	}


	private void initButtons() {
		cancelButton.disableProperty().bind(detailPane.visibleProperty().not());

		createButton.disableProperty().bind(createdNewUser);

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
		userList.getSelectionModel().selectedItemProperty().addListener((observable, wasSelected, isSelected) -> {
			if (isSelected == null)
				return;

			if (disableSelectionListener.get()) {
				disableSelectionListener.set(false);
				return;
			}

			if (resources.get("user.new").equals(nameInput.getText()) ||
			    nameInput.getText() == null || nameInput.getText().isEmpty()) {
				disableSelectionListener.set(true);
				Platform.runLater(() -> userList.getSelectionModel().select(wasSelected));
				return;
			}

			fillDetails(isSelected);
		});

		userList.setItems(model.getUsers());
	}


	@Override
	public void setNotificationPane(NotificationPane pane) {
		notificationPane = pane;
	}
}
