package de.fmk.kicknrush.models.dashboard;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.rest.RestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.SQLException;


public class DashboardModel {
	private static final Logger LOGGER = LoggerFactory.getLogger(DashboardModel.class);

	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider   cacheProvider;
	@Inject
	private DatabaseHandler dbHandler;
	@Inject
	private RestHandler     restHandler;


	public boolean logout() {
		if (!restHandler.logout()) {
			LOGGER.warn("Could not log out the user.");
			return false;
		}

		cacheProvider.clearUserCache();

		try {
			dbHandler.saveSettings(cacheProvider.getSettingCache());
		}
		catch (SQLException sqlex) {
			LOGGER.error("Could not save settings.", sqlex);
		}

		return true;
	}
}
