package de.fmk.kicknrush.models.dashboard;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.CacheProvider;
import de.fmk.kicknrush.helper.UserCacheKey;
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


	public void logout() {
		cacheProvider.clearUserCache();

		try {
			dbHandler.saveSettings(cacheProvider.getSettingsCache());
		}
		catch (SQLException sqlex) {
			LOGGER.error("Could not save settings.", sqlex);
		}
	}
}
