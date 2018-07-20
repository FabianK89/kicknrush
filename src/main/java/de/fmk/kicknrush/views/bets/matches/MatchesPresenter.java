package de.fmk.kicknrush.views.bets.matches;

import de.fmk.kicknrush.models.bets.matches.MatchesModel;
import de.fmk.kicknrush.models.bets.matches.match.MatchModel;
import de.fmk.kicknrush.models.pojo.MatchDay;
import de.fmk.kicknrush.views.INotificationPresenter;
import de.fmk.kicknrush.views.Notification;
import de.fmk.kicknrush.views.bets.matches.match.MatchPresenter;
import de.fmk.kicknrush.views.bets.matches.match.MatchView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * @author FabianK
 */
public class MatchesPresenter implements INotificationPresenter, Initializable {
	@FXML
	private Button             nextButton;
	@FXML
	private Button             previousButton;
	@FXML
	private ComboBox<MatchDay> matchCombo;
	@FXML
	private VBox               matchBox;

	@Inject
	private MatchesModel model;

	private Notification notificationPane;


	@Override
	public void enter() {
		matchCombo.getSelectionModel().selectFirst();

		model.startThread();
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		matchCombo.setItems(model.getMatchDays());
		matchCombo.setCellFactory(listView -> new MatchDayListCell());
		matchCombo.setButtonCell(new MatchDayListCell());
		matchCombo.getSelectionModel().selectedItemProperty().addListener(((observable, wasSelected, isSelected) -> {
			final int selectedIndex = matchCombo.getSelectionModel().getSelectedIndex();

			nextButton.setDisable(isSelected == null || selectedIndex == matchCombo.getItems().size() - 1);
			previousButton.setDisable(isSelected == null || selectedIndex == 0);

			buildMatches(isSelected);
		}));
	}


	private void buildMatches(final MatchDay matchDay) {
		matchBox.getChildren().clear();
		matchDay.getMatches().forEach(match -> {
			final MatchModel     matchModel;
			final MatchPresenter presenter;
			final MatchView      view;

			view = new MatchView();

			matchBox.getChildren().add(view.getView());

			matchModel = new MatchModel();
			matchModel.setMatch(match);
			matchModel.setCurrentTime(model.currentTimeProperty().get());

			model.currentTimeProperty().addListener((observable, oldValue, newValue) ->
					matchModel.setCurrentTime(newValue));

			presenter = (MatchPresenter) view.getPresenter();
			presenter.setNotificationPane(notificationPane);
			presenter.setModel(matchModel);
			presenter.enter();
		});
	}


	@Override
	public boolean leave() {
		model.stopThread();
		return true;
	}


	@Override
	public void setNotificationPane(Notification pane) {
		notificationPane = pane;
	}


	@FXML
	private void onNextMatchDay() {
		final int index = matchCombo.getSelectionModel().getSelectedIndex();

		matchCombo.getSelectionModel().clearAndSelect(index + 1);
	}


	@FXML
	private void onPreviousMatchDay() {
		final int index = matchCombo.getSelectionModel().getSelectedIndex();

		matchCombo.getSelectionModel().clearAndSelect(index - 1);
	}


	private final class MatchDayListCell extends ListCell<MatchDay> {
		@Override
		protected void updateItem(MatchDay item, boolean empty) {
			super.updateItem(item, empty);

			if (item == null || empty)
				setText(null);
			else
				setText(item.getName());
		}
	}
}
