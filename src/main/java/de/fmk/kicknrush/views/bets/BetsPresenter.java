package de.fmk.kicknrush.views.bets;

import com.airhacks.afterburner.views.FXMLView;
import de.fmk.kicknrush.views.INotificationPresenter;
import de.fmk.kicknrush.views.bets.bonus.BonusView;
import de.fmk.kicknrush.views.bets.matches.MatchesView;
import de.fmk.kicknrush.views.bets.statistic.StatisticView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.NotificationPane;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * @author FabianK
 */
public class BetsPresenter implements INotificationPresenter, Initializable {
	@FXML
	private StackPane    contentPane;
	@FXML
	private ToggleButton bonusButton;
	@FXML
	private ToggleButton matchButton;
	@FXML
	private ToggleButton statisticButton;

	private BonusView        bonusView;
	private MatchesView      matchesView;
	private NotificationPane notificationPane;
	private StatisticView    statisticView;
	private ToggleGroup      navGroup;


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
		bonusView     = new BonusView();
		matchesView   = new MatchesView();
		statisticView = new StatisticView();

		navGroup = new ToggleGroup();
		navGroup.selectedToggleProperty().addListener((observable, lastToggle, newToggle) -> {
			if (lastToggle != null && newToggle == null) {
				navGroup.selectToggle(lastToggle);
				return;
			}

			changeView(newToggle.getUserData());
		});

		initView(bonusButton, bonusView);
		initView(matchButton, matchesView);
		initView(statisticButton, statisticView);

		matchButton.setSelected(true);
	}


	private void changeView(final Object userData) {
		final FXMLView view;

		if (!(userData instanceof FXMLView))
			return;

		view = (FXMLView) userData;

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
	public void setNotificationPane(NotificationPane pane) {
		notificationPane = pane;
		navGroup.getToggles().forEach(toggle -> {
			final INotificationPresenter presenter;

			presenter = getPresenter(toggle.getUserData());

			if (presenter != null)
				presenter.setNotificationPane(pane);
		});
	}
}
