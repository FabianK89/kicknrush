package de.fmk.kicknrush.views.administration.teamadministration;

import de.fmk.kicknrush.helper.UTF8Resources;
import de.fmk.kicknrush.models.Status;
import de.fmk.kicknrush.models.administration.teamadministration.TeamAdministrationModel;
import de.fmk.kicknrush.views.INotificationPresenter;
import de.fmk.kicknrush.views.Notification;
import javafx.fxml.Initializable;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * @author FabianK
 */
public class TeamAdministrationPresenter implements INotificationPresenter, Initializable {
	@Inject
	private TeamAdministrationModel model;

	private Notification  notificationPane;
	private UTF8Resources resources;


	@Override
	public void enter() {
		model.loadTeams();
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = new UTF8Resources(resources);

		model.statusProperty().addListener((observable, oldStatus, newStatus) -> {
			if (Status.RUNNING == newStatus) {
				notificationPane.showLoading(this.resources.get("msg.loading"));
			}
			else if (Status.SUCCESS == newStatus) {
				notificationPane.hide();
			}
		});
	}


	@Override
	public boolean leave() {
		return true;
	}


	@Override
	public void setNotificationPane(Notification pane) {
		notificationPane = pane;
	}
}
