package de.fmk.kicknrush.views.administration;

import de.fmk.kicknrush.helper.UTF8Resources;
import de.fmk.kicknrush.models.administration.AdministrationModel;
import de.fmk.kicknrush.models.pojo.User;
import de.fmk.kicknrush.views.Notification;
import de.fmk.kicknrush.views.settings.ISettingsPresenter;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.NotificationPane;

import javax.inject.Inject;
import java.net.URL;
import java.util.Comparator;
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
	@FXML
	private TextField      searchInput;

	@Inject
	private AdministrationModel model;

	private FilteredList<User> filteredList;
	private Notification       notificationPane;
	private UTF8Resources      resources;


	public AdministrationPresenter() {
		createdNewUser           = new SimpleBooleanProperty(false);
		disableSelectionListener = new SimpleBooleanProperty(false);
	}


	@Override
	public void enter() {
		model.load();
		userList.refresh();
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


	private void fillDetails(final User user) {
		nameInput.setText(user.getUsername());
		adminCheck.setSelected(user.isAdmin());

		nameInput.setDisable(!createdNewUser.get());
	}


	private void initButtons() {
		cancelButton.disableProperty().bind(detailPane.visibleProperty().not());

		createButton.disableProperty().bind(createdNewUser);

		deleteButton.setText(this.resources.get("btn.delete"));
		deleteButton.disableProperty().bind(userList.getSelectionModel().selectedItemProperty().isNull());

		saveButton.disableProperty().bind(detailPane.visibleProperty().not());
	}


	private void initList() {
		filteredList = new FilteredList<>(model.getUsers(), user -> true);

		searchInput.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ESCAPE)
				searchInput.setText(null);
		});
		searchInput.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue == null || newValue.isEmpty())
				filteredList.setPredicate(user -> true);
			else
				filteredList.setPredicate(user -> user.getUsername().toLowerCase().contains(newValue.toLowerCase()));
		});

		userList.setCellFactory(list -> new UserCell());
		userList.getSelectionModel().selectedItemProperty().addListener((observable, wasSelected, isSelected) -> {
			if (isSelected == null)
				return;

			if (disableSelectionListener.get()) {
				disableSelectionListener.set(false);
				return;
			}

			if (wasSelected != null && (resources.get("user.new").equals(nameInput.getText()) ||
			    nameInput.getText() == null || nameInput.getText().isEmpty())) {
				disableSelectionListener.set(true);
				Platform.runLater(() -> userList.getSelectionModel().select(wasSelected));
				return;
			}

			fillDetails(isSelected);
		});

		userList.setItems(filteredList.sorted(Comparator.comparing(User::getUsername)));
	}


	@FXML
	private void onCancel() {
		if (createdNewUser.get()) {
			nameInput.setText(" ");
			model.removeUser(userList.getSelectionModel().getSelectedItem());
			createdNewUser.set(false);
		}

		userList.getSelectionModel().clearSelection();
	}


	@FXML
	private void onCreate() {
		final User user;

		user = model.createUser(resources.get("user.new"));

		searchInput.setText(null);
		createdNewUser.set(true);
		fillDetails(user);
		disableSelectionListener.set(true);
		userList.refresh();
		userList.getSelectionModel().select(user);
	}


	@FXML
	private void onDelete() {
		final String username;
		final User   user;

		user     = userList.getSelectionModel().getSelectedItem();
		username = user.getUsername();

		if (model.delete(user)) {
			notificationPane.show(resources.get("msg.deleted", username));
			userList.refresh();
			userList.getSelectionModel().clearSelection();
		}
	}


	@FXML
	private void onSave() {
		final User user;

		if (resources.get("user.new").equals(nameInput.getText()) ||
		    nameInput.getText() == null || nameInput.getText().isEmpty()) {
			notificationPane.show(resources.get("user.another.name"));
			return;
		}

		user = userList.getSelectionModel().getSelectedItem();
		user.setUsername(nameInput.getText());
		user.setAdmin(adminCheck.isSelected());

		if (model.save(user, createdNewUser.get())) {
			createdNewUser.set(false);
			nameInput.setDisable(true);
			userList.setItems(filteredList.sorted(Comparator.comparing(User::getUsername)));
			userList.refresh();
			notificationPane.show(resources.get("msg.saved", user.getUsername()));
		}
	}


	@Override
	public void setNotificationPane(Notification pane) {
		notificationPane = pane;
	}


	private final class UserCell extends ListCell<User> {
		@Override
		protected void updateItem(User item, boolean empty) {
			super.updateItem(item, empty);

			if (item == null || empty)
				setText(null);
			else
				setText(item.getUsername());
		}
	}
}
