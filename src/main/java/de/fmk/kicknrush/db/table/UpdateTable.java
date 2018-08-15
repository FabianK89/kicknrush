package de.fmk.kicknrush.db.table;

import de.fmk.kicknrush.helper.TimeUtils;
import de.fmk.kicknrush.models.pojo.Update;
import org.h2.api.TimestampWithTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * @author FabianK
 */
public class UpdateTable extends AbstractTable<String, Update> {
	private static final Logger LOGGER = LoggerFactory.getLogger(UpdateTable.class);

	static final String LAST_UPDATE = "LAST_UPDATE";
	static final String TABLE_NAME  = "TABLE_NAME";


	public UpdateTable() {
		super("LAST_UPDATES");

		addColumn(new Column(TABLE_NAME, TYPE.VARCHAR255, CONSTRAINT.PRIMARY_KEY));
		addColumn(new Column(LAST_UPDATE, TYPE.TIMESTAMP, CONSTRAINT.NOT_NULL));
	}


	@Override
	public boolean merge(Connection connection, Update update) {
		if (update == null)
			throw new IllegalArgumentException("The update object must not be null.");

		try (PreparedStatement statement = connection.prepareStatement(getMergeQuery())) {
			statement.setString(1, update.getTableName());
			statement.setObject(2, TimeUtils.createTimestamp(update.getLastUpdateUTC()));

			if (1 == statement.executeUpdate()) {
				LOGGER.info("Update of table '{}' at {} has been stored.", update.getTableName(), update.getLastUpdateUTC());
				return true;
			}
		}
		catch (SQLException sqlex) {
			LOGGER.error("Could not store the update of table '{}' at {}.",
			             update.getTableName(),
			             update.getLastUpdateUTC(),
			             sqlex);
		}

		return false;
	}


	@Override
	public void create(Connection connection) throws SQLException {
		if (connection == null || connection.isClosed())
			throw new IllegalStateException(NO_CONNECTION);

		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(getCreationQuery());
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while creating the update table.", sqlex);
		}
	}


	@Override
	public List<Update> selectAll(Connection connection) {
		final List<Update> updates;

		updates = new ArrayList<>();

		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery(getSelectAllQuery())) {
				while (rs.next()) {
					final LocalDateTime         time;
					final String                timeString;
					final TimestampWithTimeZone timestamp;

					timestamp  = (TimestampWithTimeZone) rs.getObject(LAST_UPDATE);
					time       = TimeUtils.convertTimestamp(timestamp);
					timeString = TimeUtils.convertLocalDateTimeUTC(time);

					updates.add(new Update(timeString, rs.getString(TABLE_NAME)));
				}
			}
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while reading the updates table.", sqlex);
		}

		return updates;
	}
}
