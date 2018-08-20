package de.fmk.kicknrush.views.bets.matches;

import de.fmk.kicknrush.models.bets.matches.MatchesModel;
import de.fmk.kicknrush.models.bets.matches.match.MatchModel;
import de.fmk.kicknrush.models.pojo.Group;
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
	private Button          nextButton;
	@FXML
	private Button          previousButton;
	@FXML
	private ComboBox<Group> groupCombo;
	@FXML
	private VBox            matchBox;

	@Inject
	private MatchesModel model;

	private Notification notificationPane;


	@Override
	public void enter() {
		groupCombo.getSelectionModel().selectFirst();

		model.startThread();
	}


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		groupCombo.setCellFactory(listView -> new GroupListCell());
		groupCombo.setButtonCell(new GroupListCell());

		groupCombo.getSelectionModel().selectedItemProperty().addListener(((observable, wasSelected, isSelected) -> {
			final int selectedIndex = groupCombo.getSelectionModel().getSelectedIndex();

			nextButton.setDisable(isSelected == null || selectedIndex == groupCombo.getItems().size() - 1);
			previousButton.setDisable(isSelected == null || selectedIndex == 0);

			buildMatches(isSelected);
		}));

		groupCombo.setItems(model.getGroups());
		model.setGroupListChangeListener(change -> {
			if (!change.next())
				return;

			groupCombo.setValue(model.getActualGroup(groupCombo.getItems()));
		});
	}


	private void buildMatches(final Group group) {
		matchBox.getChildren().clear();

		model.getMatchesForGroup(group).forEach(match -> {
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
		final int index = groupCombo.getSelectionModel().getSelectedIndex();

		groupCombo.getSelectionModel().clearAndSelect(index + 1);
	}


	@FXML
	private void onPreviousMatchDay() {
		final int index = groupCombo.getSelectionModel().getSelectedIndex();

		groupCombo.getSelectionModel().clearAndSelect(index - 1);
	}


	private final class GroupListCell extends ListCell<Group> {
		@Override
		protected void updateItem(Group item, boolean empty) {
			super.updateItem(item, empty);

			if (item == null || empty)
				setText(null);
			else
				setText(item.getGroupName());
		}
	}
}
