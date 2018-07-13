package de.fmk.kicknrush.views.groups;

import de.fmk.kicknrush.views.INotificationPresenter;
import javafx.fxml.Initializable;
import org.controlsfx.control.NotificationPane;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * @author FabianK
 */
public class GroupsPresenter implements INotificationPresenter, Initializable {
	@Override
	public void enter() {

	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}


	@Override
	public boolean leave() {
		return true;
	}


	@Override
	public void setNotificationPane(NotificationPane pane) {

	}
}
