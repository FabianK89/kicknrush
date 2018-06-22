package de.fmk.kicknrush.models.login;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.ApplicationHelper;
import de.fmk.kicknrush.helper.CacheProvider;
import de.fmk.kicknrush.helper.UserCacheKey;
import de.fmk.kicknrush.models.Status;
import de.fmk.kicknrush.models.pojo.User;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.sql.SQLException;


/**
 * Model of the login view.
 *
 * @author Fabian Kiesl
 */
public class LoginModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginModel.class);

	@Inject
	private ApplicationHelper appHelper;
	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider     cacheProvider;
	@Inject
	private DatabaseHandler   dbHandler;

	private ObjectProperty<Status> status;


	@PostConstruct
	public void init() {
		status = new SimpleObjectProperty<>(Status.INITIAL);
	}


	public ObjectProperty<Status> statusProperty()
	{
		return status;
	}


	public void loadSettings() {
		final Thread databaseThread;

		databaseThread = new Thread(() -> {
			try {
				dbHandler.loadSettings(cacheProvider);
			}
			catch (SQLException sqlex) {
				LOGGER.error("An error occurred while loading the settings from the database.", sqlex);
			}

			try {
				if (cacheProvider.getSettingsCache().isEmpty())
					appHelper.fillSettingsCache();
			}
			catch (IOException ioex) {
				LOGGER.error("Could not read initial settings.", ioex);
			}
		});

		databaseThread.start();
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
			User user = null;

			try {
				user = dbHandler.loginUser(username, password);
			}
			catch (SQLException sqlex) {
				LOGGER.error("An error occurred while checking login data of user '{}'.", username, sqlex);
			}

			if (user != null) {
				cacheProvider.putUserValue(UserCacheKey.USER_ID, user.getId());
				cacheProvider.putUserValue(UserCacheKey.USERNAME, user.getUsername());
				cacheProvider.putUserValue(UserCacheKey.IS_ADMIN, Boolean.valueOf("admin".equals(user.getUsername())).toString());
				cacheProvider.putUserValue(UserCacheKey.PASSWORD, user.getPassword());
				Platform.runLater(() -> status.set(Status.SUCCESS));
			}
			else {
				cacheProvider.removeUserValue(UserCacheKey.USER_ID);
				cacheProvider.removeUserValue(UserCacheKey.USERNAME);
				cacheProvider.removeUserValue(UserCacheKey.PASSWORD);
				cacheProvider.removeUserValue(UserCacheKey.IS_ADMIN);
				Platform.runLater(() -> status.set(Status.FAILED));
			}
		});

		loginThread.start();
	}
}
