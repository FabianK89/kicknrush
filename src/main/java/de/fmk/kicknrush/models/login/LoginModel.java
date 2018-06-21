package de.fmk.kicknrush.models.login;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.models.Status;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.sql.SQLException;


/**
 * Model of the login view.
 *
 * @author Fabian Kiesl
 */
public class LoginModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginModel.class);

	@Inject
	private DatabaseHandler dbHandler;

	private ObjectProperty<Status> status;


	@PostConstruct
	public void init() {
		status = new SimpleObjectProperty<>(Status.INITIAL);
	}


	public ObjectProperty<Status> statusProperty()
	{
		return status;
	}


	/**
	 * Start the login process in an own thread and set the status to {@link de.fmk.kicknrush.models.Status#RUNNING}
	 * while the thread is running. When finished, set the status to {@link de.fmk.kicknrush.models.Status#SUCCESS} if
	 * the login was successful, otherwise set the status to {@link de.fmk.kicknrush.models.Status#FAILED}.
	 * @param username The name of the user.
	 * @param password The password of the user.
	 */
	public void login(final String username, final String password) {
		final Thread loginThread;

		status.set(Status.RUNNING);

		loginThread = new Thread(() -> {
			boolean canLogin = false;

			try {
				canLogin = dbHandler.loginUser(username, password);
			}
			catch (SQLException sqlex) {
				LOGGER.error("An error occurred while checking login data of user '{}'.", username, sqlex);
			}

			if (canLogin)
				Platform.runLater(() -> status.set(Status.SUCCESS));
			else
				Platform.runLater(() -> status.set(Status.FAILED));
		});

		loginThread.start();
	}
}
