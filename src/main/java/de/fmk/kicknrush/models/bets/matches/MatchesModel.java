package de.fmk.kicknrush.models.bets.matches;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.ThreadHelper;
import de.fmk.kicknrush.models.AbstractStatusModel;
import de.fmk.kicknrush.models.pojo.Group;
import de.fmk.kicknrush.models.pojo.Match;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author FabianK
 */
public class MatchesModel extends AbstractStatusModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(MatchesModel.class);

	private final BooleanProperty                   stopThreadProperty;
	private final Map<Group, ObservableList<Match>> groupMap;
	private final ObjectProperty<LocalDateTime>     currentTimeProperty;
	private final ObservableList<Group>             groups;
	private final Thread                            currentTimeThread;

	@Inject
	private DatabaseHandler dbHandler;
	@Inject
	private ThreadHelper    threadHelper;


	public MatchesModel() {
		super();

		currentTimeProperty = new SimpleObjectProperty<>();
		groupMap            = new HashMap<>();
		groups              = FXCollections.observableArrayList();
		stopThreadProperty  = new SimpleBooleanProperty(false);

		currentTimeThread = new Thread(() -> {
			do {
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


	public ObservableList<Match> getMatchesForGroup(Group group) {
		if (group == null || !groupMap.containsKey(group))
			return FXCollections.emptyObservableList();

		return groupMap.get(group);
	}


	public Group getActualGroup(List<Group> groupList) {
		final LocalDateTime now;

		Group actualGroup = null;

		now = LocalDateTime.now();

		for (final Group group: groupList) {
			if (actualGroup == null)
				actualGroup = group;
			else
				actualGroup = calculateNearestKickOff(actualGroup, group, now);
		}

		return actualGroup;
	}


	private Group calculateNearestKickOff(Group lastGroup, Group nextGroup, LocalDateTime now)
	{
		final LocalDateTime lastKickOff;
		final LocalDateTime nextKickOff;

		long differenceBefore;
		long differenceAfter;

		if (lastGroup.getLastKickOff() == null)
			return nextGroup;

		if (nextGroup.getFirstKickOff() == null)
			return lastGroup;

		lastKickOff = lastGroup.getLastKickOff();
		nextKickOff = nextGroup.getFirstKickOff();

		differenceBefore = lastKickOff.until(now, ChronoUnit.SECONDS);
		differenceAfter = now.until(nextKickOff, ChronoUnit.SECONDS);

		if (differenceBefore > differenceAfter)
			return nextGroup;
		else
			return lastGroup;
	}


	@PostConstruct
	public void init() {
		loadGroups();
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


	public ObservableList<Group> getGroups() {
		return groups;
	}


	public void setGroupListChangeListener(ListChangeListener<Group> listener) {
		groups.addListener(listener);
	}


	private void loadGroups() {
		final Thread thread;

		groups.clear();
		groupMap.clear();

		thread = new Thread(() -> {
			final List<Group> tmpGroups;

			while (threadHelper.isThreadRunning(ThreadHelper.UPDATE_THREAD)) {
				try {
					Thread.sleep(50L);
				}
				catch (InterruptedException iex) {
					LOGGER.error("Thread to check the update state was interrupted.", iex);
					Thread.currentThread().interrupt();
				}
			}

			tmpGroups = dbHandler.selectAllGroups();
			tmpGroups.forEach(group ->
					groupMap.put(group, FXCollections.observableArrayList(dbHandler.selectMatchesForGroup(group))));

			Platform.runLater(() -> groups.addAll(tmpGroups));

			threadHelper.removeThread(Thread.currentThread().getName());
		}, "loadGroups");

		threadHelper.addThread(thread);
		thread.start();
	}
}
