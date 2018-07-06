package de.fmk.kicknrush.models.login;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.ApplicationHelper;
import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.UserCache;
import de.fmk.kicknrush.helper.cache.UserCacheKey;
import de.fmk.kicknrush.models.Status;
import de.fmk.kicknrush.models.pojo.User;
import de.fmk.kicknrush.rest.RestHandler;
import de.fmk.kicknrush.security.PasswordUtils;
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
 * @author FabianK
 */
public class LoginModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginModel.class);

	@Inject
	private ApplicationHelper appHelper;
	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider     cacheProvider;
	@Inject
	private DatabaseHandler   dbHandler;
	@Inject
	private RestHandler       restHandler;

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
				if (cacheProvider.getSettingCache().isEmpty())
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
			final String    securePassword;
			final User      user;
			final UserCache cache;

			String salt;

			try {
				salt           = dbHandler.readSalt(username);
				securePassword = salt == null ? password : PasswordUtils.generateSecurePassword(password, salt);
			}
			catch (SQLException sqlex) {
				LOGGER.error("Could not connect to internal data base.", sqlex);
				Platform.runLater(() -> status.set(Status.FAILED));
				return;
			}

			user  = restHandler.loginUser(username, securePassword);
			cache = cacheProvider.getUserCache();

			if (user != null) {
				if (user.getSalt() == null) {
					cache.putBooleanValue(UserCacheKey.CHANGE_PWD, true);
				}
				else if (salt == null) {
					try {
						dbHandler.updateUser(user);
					}
					catch (SQLException sqlex) {
						LOGGER.error("Could not connect to internal data base.", sqlex);
					}
				}

				cache.putStringValue(UserCacheKey.USER_ID, user.getId());
				cache.putStringValue(UserCacheKey.USERNAME, user.getUsername());
				cache.putBooleanValue(UserCacheKey.IS_ADMIN, user.isAdmin());
				cache.putStringValue(UserCacheKey.PASSWORD, user.getPassword());
				cache.putStringValue(UserCacheKey.SALT, user.getSalt());
				cache.putStringValue(UserCacheKey.SESSION, user.getSessionID());
				Platform.runLater(() -> status.set(Status.SUCCESS));
			}
			else {
				cache.removeValue(UserCacheKey.USER_ID);
				cache.removeValue(UserCacheKey.USERNAME);
				cache.removeValue(UserCacheKey.PASSWORD);
				cache.removeValue(UserCacheKey.IS_ADMIN);
				cache.removeValue(UserCacheKey.SALT);
				cache.removeValue(UserCacheKey.SESSION);
				Platform.runLater(() -> status.set(Status.FAILED));
			}
		});

		loginThread.start();
	}
}
