package de.fmk.kicknrush.views.administration;

import com.airhacks.afterburner.views.FXMLView;
import de.fmk.kicknrush.views.INotificationPresenter;
import de.fmk.kicknrush.views.Notification;
import de.fmk.kicknrush.views.administration.resultadministration.ResultAdministrationView;
import de.fmk.kicknrush.views.administration.teamadministration.TeamAdministrationView;
import de.fmk.kicknrush.views.administration.useradministration.UserAdministrationView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;


public class AdministrationPresenter implements INotificationPresenter, Initializable {
	@FXML
	private StackPane    contentPane;
	@FXML
	private ToggleButton resultButton;
	@FXML
	private ToggleButton teamButton;
	@FXML
	private ToggleButton userButton;

	private Notification             notificationPane;
	private ResultAdministrationView resultView;
	private ToggleGroup              navGroup;
	private TeamAdministrationView   teamView;
	private UserAdministrationView   userView;


	@Override
	public void enter() {
		navGroup.getToggles().forEach(toggle -> {
			final INotificationPresenter presenter;

			presenter = getPresenter(toggle.getUserData());

			if (presenter != null)
				presenter.enter();
		});
	}


	private INotificationPresenter getPresenter(final Object userData) {
		final FXMLView view;

		if (!(userData instanceof FXMLView))
			return null;

		view = (FXMLView) userData;

		if (!(view.getPresenter() instanceof INotificationPresenter))
			return null;

		return (INotificationPresenter) view.getPresenter();
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		resultView = new ResultAdministrationView();
		teamView   = new TeamAdministrationView();
		userView   = new UserAdministrationView();

		navGroup = new ToggleGroup();
		navGroup.selectedToggleProperty().addListener((observable, lastToggle, newToggle) -> {
			if (lastToggle != null && newToggle == null) {
				navGroup.selectToggle(lastToggle);
				return;
			}

			changeView(newToggle.getUserData());
		});

		initView(resultButton, resultView);
		initView(teamButton, teamView);
		initView(userButton, userView);

		userButton.setSelected(true);
	}


	private void changeView(final Object userData) {
		final FXMLView view;

		if (!(userData instanceof FXMLView))
			return;

		view = (FXMLView) userData;

		if (contentPane.getChildren() != null)
			contentPane.getChildren().clear();

		contentPane.getChildren().add(view.getView());
	}


	private void initView(final ToggleButton button, final FXMLView view) {
		button.setToggleGroup(navGroup);
		button.setUserData(view);
	}


	@Override
	public boolean leave() {
		navGroup.getToggles().forEach(toggle -> {
			final INotificationPresenter presenter;

			presenter = getPresenter(toggle.getUserData());

			if (presenter != null)
				presenter.leave();
		});

		return true;
	}


	@Override
	public void setNotificationPane(Notification pane) {
		notificationPane = pane;
		navGroup.getToggles().forEach(toggle -> {
			final INotificationPresenter presenter;

			presenter = getPresenter(toggle.getUserData());

			if (presenter != null)
				presenter.setNotificationPane(pane);
		});
	}
}
