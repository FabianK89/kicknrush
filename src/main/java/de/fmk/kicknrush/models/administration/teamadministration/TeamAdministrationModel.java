package de.fmk.kicknrush.models.administration.teamadministration;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.ThreadHelper;
import de.fmk.kicknrush.helper.UpdateHelper;
import de.fmk.kicknrush.models.AbstractStatusModel;
import de.fmk.kicknrush.models.Status;
import de.fmk.kicknrush.models.pojo.Team;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;


/**
 * @author FabianK
 */
public class TeamAdministrationModel extends AbstractStatusModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamAdministrationModel.class);

	private final ObservableList<Team> teams;

	@Inject
	private DatabaseHandler dbHandler;
	@Inject
	private UpdateHelper    updateHelper;
	@Inject
	private ThreadHelper    threadHelper;


	public TeamAdministrationModel() {
		super();

		teams = FXCollections.observableArrayList();
	}


	public void saveChanges(final Team team) {
		final Thread thread;

		if (team == null)
			throw new IllegalArgumentException("The team object must not be null.");

		statusProperty.set(Status.RUNNING);

		thread = new Thread(() -> {
			if (dbHandler.mergeTeam(team))
				Platform.runLater(() -> statusProperty().set(Status.SUCCESS));
			else
				Platform.runLater(() -> statusProperty().set(Status.FAILED));

			threadHelper.removeThread(Thread.currentThread().getName());
		}, "saveTeam".concat(Integer.toString(team.getTeamId())));

		threadHelper.addThread(thread);

		thread.start();
	}


	@PostConstruct
	public void init() {

	}


	public void loadTeams() {
		final Thread thread;

		statusProperty.set(Status.RUNNING);

		thread = new Thread(() -> {
			final List<Team> dbTeams;

			updateHelper.checkForUpdates();

			dbTeams = dbHandler.selectAllTeams();

			Platform.runLater(() -> {
				teams.clear();
				teams.addAll(dbTeams);
				statusProperty().set(Status.SUCCESS);
			});

			threadHelper.removeThread(Thread.currentThread().getName());
		}, "updateTeams");

		threadHelper.addThread(thread);

		thread.start();
	}


	public Image getImage(final String imagePath) {
		final Path path;

		if (imagePath == null)
			return null;

		if (isWebUrl(imagePath))
			return new Image(imagePath);

		path = Paths.get(imagePath);

		if (imagePath.toLowerCase().endsWith(".png") && Files.isRegularFile(path)) {
			try (InputStream is = Files.newInputStream(path)) {
				return new Image(is);
			}
			catch (IOException ioex) {
				LOGGER.error("Could not load the image file '{}'.", imagePath, ioex);
			}
		}

		return null;
	}


	public boolean isWebUrl(final String url) {
		return url != null && url.matches("http[s]?://.+");
	}


	public ObservableList<Team> getTeams() {
		return teams;
	}
}
