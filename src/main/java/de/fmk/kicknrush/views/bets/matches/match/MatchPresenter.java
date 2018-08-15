package de.fmk.kicknrush.views.bets.matches.match;

import de.fmk.kicknrush.helper.UTF8Resources;
import de.fmk.kicknrush.models.bets.matches.match.MatchModel;
import de.fmk.kicknrush.views.INotificationPresenter;
import de.fmk.kicknrush.views.Notification;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.PopOver;

import java.net.URL;
import java.util.ResourceBundle;


/**
 * @author FabianK
 */
public class MatchPresenter implements INotificationPresenter, Initializable {
	@FXML
	private GridPane  contentGrid;
	@FXML
	private GridPane  resultGrid;
	@FXML
	private ImageView guestLogo;
	@FXML
	private ImageView homeLogo;
	@FXML
	private Label     guestTeamLabel;
	@FXML
	private Label     homeTeamLabel;
	@FXML
	private Label     kickOffLabel;
	@FXML
	private Label     matchStatusLabel;
	@FXML
	private Label     moreInfoLabel;
	@FXML
	private TextField guestBetInput;
	@FXML
	private TextField homeBetInput;

	private MatchModel    model;
	private Notification  notificationPane;
	private UTF8Resources resources;


	@Override
	public void enter() {
		if (model == null)
			throw new IllegalStateException("The model must be initialized first.");

		kickOffLabel.setText(model.getKickOffTimeString());
		guestTeamLabel.setText(model.getMatch().getGuestTeam().getTeamName());
		homeTeamLabel.setText(model.getMatch().getHomeTeam().getTeamName());
		guestBetInput.disableProperty().bind(model.betsClosedProperty());
		homeBetInput.disableProperty().bind(model.betsClosedProperty());

		if (model.getMatchStatus() != null) {
			matchStatusLabel.setText(resources.get(model.getMatchStatus()));

			if (!contentGrid.getChildren().contains(resultGrid))
				contentGrid.add(resultGrid, 0, 4, Integer.MAX_VALUE, 1);
		}
		else {
			contentGrid.getChildren().remove(resultGrid);
		}

		model.matchStatusProperty().addListener((observable, oldStatus, newStatus) -> {
			if (newStatus == null) {
				matchStatusLabel.setText(null);
				contentGrid.getChildren().remove(resultGrid);
			}
			else {
				matchStatusLabel.setText(resources.get(newStatus));

				if (!contentGrid.getChildren().contains(resultGrid))
					contentGrid.add(resultGrid, 0, 4, Integer.MAX_VALUE, 1);
			}
		});

		model.init();
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.resources = new UTF8Resources(resources);

		guestBetInput.textProperty().addListener(getBetInputListener(guestBetInput));
		guestBetInput.focusedProperty().addListener(getBetFocusListener());
		homeBetInput.textProperty().addListener(getBetInputListener(homeBetInput));
		homeBetInput.focusedProperty().addListener(getBetFocusListener());

		initMoreInfoLabel();
	}


	private void initMoreInfoLabel() {
		final PopOver popOver;

		popOver = new PopOver();
		popOver.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
		popOver.setDetachable(false);

		moreInfoLabel.setOnMouseEntered(event -> moreInfoLabel.setCursor(Cursor.HAND));
		moreInfoLabel.setOnMouseExited(event -> moreInfoLabel.setCursor(Cursor.DEFAULT));
		moreInfoLabel.setOnMouseClicked(event -> popOver.show(moreInfoLabel));
	}


	private ChangeListener<String> getBetInputListener(TextField tf) {
		return (observable, oldInput, newInput) -> {
			final int bet;

			if (newInput == null || newInput.isEmpty())
				return;

			try {
				bet = Integer.parseInt(newInput);

				if (bet > 99)
					tf.setText(oldInput);
			}
			catch (NumberFormatException nfex) {
				tf.setText(oldInput);
			}
		};
	}


	private ChangeListener<Boolean> getBetFocusListener() {
		return ((observable, wasFocused, isFocused) -> {
			if (isFocused ||
			    guestBetInput.getText() == null || guestBetInput.getText().isEmpty() ||
			    homeBetInput.getText() == null  || homeBetInput.getText().isEmpty())
				return;

			notificationPane.show("Spiel wurde gespeichert.");
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


	public void setModel(MatchModel model) {
		this.model = model;
	}
}
