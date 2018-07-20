package de.fmk.kicknrush.models.bets.matches;

import de.fmk.kicknrush.helper.ThreadHelper;
import de.fmk.kicknrush.models.AbstractStatusModel;
import de.fmk.kicknrush.models.pojo.Match;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


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
		loadMatchDays();
	}


	public void startThread() {
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
		matchDays.addAll(createMatchDay("1. Spieltag"),
		                 createMatchDay("2. Spieltag"),
		                 createMatchDay("3. Spieltag"),
		                 createMatchDay("4. Spieltag"),
		                 createMatchDay("5. Spieltag"),
		                 createMatchDay("6. Spieltag"),
		                 createMatchDay("7. Spieltag"),
		                 createMatchDay("8. Spieltag"),
		                 createMatchDay("9. Spieltag"),
		                 createMatchDay("10. Spieltag"),
		                 createMatchDay("11. Spieltag"),
		                 createMatchDay("12. Spieltag"),
		                 createMatchDay("13. Spieltag"),
		                 createMatchDay("14. Spieltag"),
		                 createMatchDay("15. Spieltag"),
		                 createMatchDay("16. Spieltag"),
		                 createMatchDay("17. Spieltag"),
		                 createMatchDay("18. Spieltag"),
		                 createMatchDay("19. Spieltag"),
		                 createMatchDay("20. Spieltag"),
		                 createMatchDay("21. Spieltag"),
		                 createMatchDay("22. Spieltag"),
		                 createMatchDay("23. Spieltag"),
		                 createMatchDay("24. Spieltag"),
		                 createMatchDay("25. Spieltag"),
		                 createMatchDay("26. Spieltag"),
		                 createMatchDay("27. Spieltag"),
		                 createMatchDay("28. Spieltag"),
		                 createMatchDay("29. Spieltag"),
		                 createMatchDay("30. Spieltag"),
		                 createMatchDay("31. Spieltag"),
		                 createMatchDay("32. Spieltag"),
		                 createMatchDay("33. Spieltag"),
		                 createMatchDay("34. Spieltag"));
	}


	private MatchDay createMatchDay(final String name) {
		final List<Match>   matches;
		final LocalDateTime kickOff;
		final MatchDay      day;

		matches = new ArrayList<>();
		day     = new MatchDay();
		kickOff = LocalDateTime.now();

		matches.add(new Match(false, kickOff.plusMinutes(1), UUID.randomUUID().toString(), "SC Freiburg", "FC Bayern München"));
		matches.add(new Match(false, kickOff.plusMinutes(2), UUID.randomUUID().toString(), "VfB Stuttgart", "Hertha BSC Berlin"));
		matches.add(new Match(false, kickOff.plusMinutes(2), UUID.randomUUID().toString(), "VfL Wolfsburg", "Hamburger SV"));
		matches.add(new Match(false, kickOff.plusMinutes(2), UUID.randomUUID().toString(), "Borussia Dortmund", "Borussia Mönchengladbach"));
		matches.add(new Match(false, kickOff.plusMinutes(2), UUID.randomUUID().toString(), "Bayer 04 Leverkusen", "RB Leipzig"));
		matches.add(new Match(false, kickOff.plusMinutes(3), UUID.randomUUID().toString(), "Eintracht Frankfurt", "1. FC Köln"));
		matches.add(new Match(false, kickOff.plusMinutes(3), UUID.randomUUID().toString(), "Werder Bremen", "1. FC Kaiserslautern"));
		matches.add(new Match(false, kickOff.plusMinutes(3), UUID.randomUUID().toString(), "FC Schalke 04", "TSG 1899 Hoffenheim"));
		matches.add(new Match(false, kickOff.plusMinutes(3), UUID.randomUUID().toString(), "Arminia Bielefeld", "Holstein Kiel"));

		day.setName(name);
		day.setMatches(matches);

		return day;
	}
}
