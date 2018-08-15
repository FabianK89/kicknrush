package de.fmk.kicknrush.helper;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.db.table.TeamTable;
import de.fmk.kicknrush.models.pojo.Update;
import de.fmk.kicknrush.service.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author FabianK
 */
public class UpdateHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateHelper.class);

	@Inject
	private DatabaseHandler dbHandler;
	@Inject
	private RestService     restService;


	@PostConstruct
	public void init() {
		LOGGER.debug("UpdateHelper was initialized.");
	}


	public void checkForUpdates() {
		final List<Update>        serverUpdates;
		final Map<String, Update> localUpdates;

		serverUpdates = restService.getUpdates();
		localUpdates  = new HashMap<>();

		if (serverUpdates.isEmpty())
			return;

		dbHandler.selectAllUpdates().forEach(localUpdate -> localUpdates.put(localUpdate.getTableName(), localUpdate));

		serverUpdates.forEach(serverUpdate -> {
			final boolean keyExists;
			final long    lastServerUpdate;
			final long    lastLocalUpdate;
			final String  tableName;

			tableName = serverUpdate.getTableName();
			keyExists = localUpdates.containsKey(tableName);

			lastServerUpdate = TimeUtils.createTimestamp(serverUpdate.getLastUpdateUTC()).getYMD();
			lastLocalUpdate  = keyExists ?
			                   TimeUtils.createTimestamp(localUpdates.get(tableName).getLastUpdateUTC()).getYMD() :
			                   Long.MAX_VALUE;

			if (!keyExists || lastServerUpdate > lastLocalUpdate) {
					update(serverUpdate.getTableName());
					dbHandler.storeUpdate(serverUpdate);
			}
		});
	}


	private void update(final String tableName) {
		if (TeamTable.TABLE_NAME.equals(tableName))
			restService.getTeams().forEach(team -> dbHandler.mergeTeam(team));
	}
}
