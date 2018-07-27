package de.fmk.kicknrush.views.administration.teamadministration;

import de.fmk.kicknrush.helper.UTF8Resources;
import de.fmk.kicknrush.models.Status;
import de.fmk.kicknrush.models.administration.teamadministration.TeamAdministrationModel;
import de.fmk.kicknrush.models.pojo.Team;
import de.fmk.kicknrush.views.AppImage;
import de.fmk.kicknrush.views.INotificationPresenter;
import de.fmk.kicknrush.views.Notification;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import javax.inject.Inject;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * @author FabianK
 */
public class TeamAdministrationPresenter implements INotificationPresenter, Initializable {
	@FXML
	private Button         cancelButton;
	@FXML
	private Button         saveButton;
	@FXML
	private GridPane       detailPane;
	@FXML
	private ImageView      icon;
	@FXML
	private ImageView      smallIcon;
	@FXML
	private ListView<Team> teamList;
	@FXML
	private TextField      nameInput;
	@FXML
	private TextField      iconPathInput;
	@FXML
	private TextField      smallIconPathInput;

	@Inject
	private TeamAdministrationModel model;

	private Notification  notificationPane;
	private Team          selectedTeam;
	private UTF8Resources resources;


	@Override
	public void enter() {
		model.loadTeams();
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = new UTF8Resources(resources);

		nameInput.textProperty().addListener(observable -> disableSaveButton());

		iconPathInput.textProperty().addListener((pathInputObservable, oldPath, newPath) -> {
			icon.setImage(model.getImage(newPath));
			disableSaveButton();
		});
		smallIconPathInput.textProperty().addListener((pathInputObservable, oldPath, newPath) -> {
			smallIcon.setImage(model.getImage(newPath));
			disableSaveButton();
		});

		teamList.setItems(model.getTeams());
		teamList.setCellFactory(list -> new TeamCell());
		teamList.getSelectionModel().selectedItemProperty().addListener((observable, last, current) -> {
			selectedTeam = current;

			detailPane.setVisible(current != null);

			if (current == null)
				return;

			nameInput.setText(current.getTeamName());

			smallIconPathInput.setText(current.getTeamIconUrlSmall());
			iconPathInput.setText(current.getTeamIconUrl());
			saveButton.setDisable(true);
		});

		model.statusProperty().addListener((observable, oldStatus, newStatus) -> {
			if (Status.RUNNING == newStatus) {
				notificationPane.showLoading(this.resources.get("msg.loading"));
			}
			else if (Status.SUCCESS == newStatus) {
				notificationPane.showSuccess("Test", AppImage.SUCCESS);
			}
		});

		detailPane.setVisible(false);
	}


	private void disableSaveButton() {
		final boolean changedIcon;
		final boolean changedName;
		final boolean changedSmallIcon;

		changedName      = !selectedTeam.getTeamName().equals(nameInput.getText());
		changedIcon      = !selectedTeam.getTeamIconUrl().equals(iconPathInput.getText());
		changedSmallIcon = !selectedTeam.getTeamIconUrlSmall().equals(smallIconPathInput.getText());

		saveButton.setDisable(nameInput.getText() == null || icon.getImage() == null || smallIcon.getImage() == null ||
		                      !changedName && !changedIcon && !changedSmallIcon);
	}


	@Override
	public boolean leave() {
		return true;
	}


	@Override
	public void setNotificationPane(Notification pane) {
		notificationPane = pane;
	}


	@FXML
	private void onCancel() {
		teamList.getSelectionModel().clearSelection();
		teamList.refresh();
	}


	@FXML
	private void onChooseIcon() {
		iconPathInput.setText(chooseIcon(iconPathInput.getText()));
	}


	@FXML
	private void onChooseSmallIcon() {
		smallIconPathInput.setText(chooseIcon(smallIconPathInput.getText()));
	}


	@FXML
	private void onSave() {
		selectedTeam.setTeamName(nameInput.getText());
		selectedTeam.setTeamIconUrl(iconPathInput.getText());
		selectedTeam.setTeamIconUrlSmall(smallIconPathInput.getText());

		model.saveChanges(selectedTeam);

		teamList.refresh();
	}


	private String chooseIcon(final String path) {
		final File        imageFile;
		final FileChooser fc;

		fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(resources.get("file.images"), "*.png"));

		if (!model.isWebUrl(path))
			fc.setInitialFileName(path);

		imageFile = fc.showOpenDialog(teamList.getScene().getWindow());

		if (imageFile != null && imageFile.exists())
			return imageFile.getAbsolutePath();

		return path;
	}


	private class TeamCell extends ListCell<Team> {
		@Override
		protected void updateItem(Team team, boolean empty) {
			super.updateItem(team, empty);

			if (team == null || empty)
				setText(null);
			else
				setText(team.getTeamName());
		}
	}
}
