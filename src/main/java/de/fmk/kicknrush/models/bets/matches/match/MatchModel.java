package de.fmk.kicknrush.models.bets.matches.match;

import de.fmk.kicknrush.models.pojo.Match;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author FabianK
 */
public class MatchModel {
	private static final String FINISHED = "finished";
	private static final String RUNNING  = "running";

	private final BooleanProperty               betsClosedProperty;
	private final ObjectProperty<LocalDateTime> currentTimeProperty;
	private final StringProperty                matchStatusProperty;

	private Match match;


	public MatchModel() {
		betsClosedProperty  = new SimpleBooleanProperty(false);
		currentTimeProperty = new SimpleObjectProperty<>();
		matchStatusProperty = new SimpleStringProperty();

		currentTimeProperty.addListener((observable, oldTime, newTime) -> {
			betsClosedProperty.set(newTime == null || match == null || newTime.isAfter(match.getKickOff()));

			if (betsClosedProperty.get() && (match == null || match.isFinished()))
				matchStatusProperty.set(FINISHED);
			else if (betsClosedProperty.get())
				matchStatusProperty.set(RUNNING);
			else
				matchStatusProperty.set(null);
		});
	}


	public void init() {
		final LocalDateTime time = currentTimeProperty.get();

		betsClosedProperty.set(time == null || match == null || time.isAfter(match.getKickOff()));

		if (betsClosedProperty.get() && (match == null || match.isFinished()))
			matchStatusProperty.set(FINISHED);
		else if (betsClosedProperty.get())
			matchStatusProperty.set(RUNNING);
		else
			matchStatusProperty.set(null);
	}


	public String getKickOffTimeString() {
		final DateTimeFormatter formatter;
		final LocalDateTime     kickOff;

		if (match == null)
			throw new IllegalStateException("The match object must be set first.");

		formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm");
		kickOff   = match.getKickOff();

		return kickOff.format(formatter);
	}


	public Match getMatch() {
		return match;
	}


	public void setMatch(Match match) {
		this.match = match;
	}


	public BooleanProperty betsClosedProperty() {
		return betsClosedProperty;
	}


	public void setCurrentTime(LocalDateTime time) {
		currentTimeProperty.set(time);
	}


	public StringProperty matchStatusProperty() {
		return matchStatusProperty;
	}


	public String getMatchStatus() {
		return matchStatusProperty.get();
	}
}
