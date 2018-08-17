package de.fmk.kicknrush.models.dashboard;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.ThreadHelper;
import de.fmk.kicknrush.helper.UpdateHelper;
import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.service.RestService;
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
	private RestService     restService;
	@Inject
	private ThreadHelper    threadHelper;
	@Inject
	private UpdateHelper    updateHelper;


	public void checkForUpdates() {
		final Thread updateThread;

		updateThread = new Thread(() -> updateHelper.checkForUpdates());

		threadHelper.addThread(updateThread);
		updateThread.start();
	}


	public boolean logout() {
		if (!restService.logout()) {
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
