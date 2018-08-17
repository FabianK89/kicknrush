package de.fmk.kicknrush.models.bets.matches;

import de.fmk.kicknrush.helper.ThreadHelper;
import de.fmk.kicknrush.models.AbstractStatusModel;
import de.fmk.kicknrush.models.pojo.MatchDay;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;


/**
 * @author FabianK
 */
public class MatchesModel extends AbstractStatusModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(MatchesModel.class);

	private final BooleanProperty               stopThreadProperty;
	private final ObjectProperty<LocalDateTime> currentTimeProperty;
	private final ObservableList<MatchDay>      matchDays;
	private final Thread                        currentTimeThread;

	@Inject
	private ThreadHelper threadHelper;


	public MatchesModel() {
		super();

		currentTimeProperty = new SimpleObjectProperty<>();
		matchDays           = FXCollections.observableArrayList();
		stopThreadProperty  = new SimpleBooleanProperty(false);

		currentTimeThread = new Thread(() -> {
			do
			{
				Platform.runLater(() -> currentTimeProperty.set(LocalDateTime.now()));

				try {
					Thread.sleep(Duration.ofSeconds(30).toMillis());
				}
				catch (InterruptedException iex) {
					LOGGER.info("The thread to update the current time was interrupted.");
					Thread.currentThread().interrupt();
					return;
				}
			}
			while (!stopThreadProperty.get());
		}, "currentTimeThread");
	}


	@PostConstruct
	public void init() {
	}


	public synchronized void startThread() {
		if (!currentTimeThread.isAlive()) {
			threadHelper.addThread(currentTimeThread);
			stopThreadProperty.set(false);
			currentTimeThread.start();
		}
	}


	public void stopThread() {
		stopThreadProperty.set(true);
	}


	public ObjectProperty<LocalDateTime> currentTimeProperty() {
		return currentTimeProperty;
	}


	public ObservableList<MatchDay> getMatchDays() {
		return FXCollections.unmodifiableObservableList(matchDays);
	}


	private void loadMatchDays() {
		matchDays.clear();
	}
}
