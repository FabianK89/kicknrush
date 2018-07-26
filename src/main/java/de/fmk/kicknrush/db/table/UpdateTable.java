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

	private static final String NAME = "LAST_UPDATES";


	public UpdateTable()
	{
		super(NAME);
	}


	@Override
	public boolean merge(Connection connection, Update update) {
		if (update == null)
			throw new IllegalArgumentException("The update object must not be null.");

		try (PreparedStatement statement = connection.prepareStatement(getMergeQuery(COLUMN.values().length))) {
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
		final Column[] columns;

		columns = new Column[] {
				new Column().name(COLUMN.TABLE_NAME.getValue()).type(TYPE.VARCHAR255).constraint(CONSTRAINT.PRIMARY_KEY),
				new Column().name(COLUMN.LAST_UPDATE.getValue()).type(TYPE.TIMESTAMP).constraint(CONSTRAINT.NOT_NULL)
		};

		if (connection == null || connection.isClosed())
			throw new IllegalStateException(NO_CONNECTION);

		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(getCreationQuery(columns));
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
				if (rs.next()) {
					final LocalDateTime         time;
					final String                timeString;
					final TimestampWithTimeZone timestamp;

					timestamp  = (TimestampWithTimeZone) rs.getObject(COLUMN.LAST_UPDATE.getValue());
					time       = TimeUtils.convertTimestamp(timestamp);
					timeString = TimeUtils.convertLocalDateTimeUTC(time);

					updates.add(new Update(timeString, rs.getString(COLUMN.TABLE_NAME.getValue())));
				}
			}
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while reading the updates table.", sqlex);
		}

		return updates;
	}


	private enum COLUMN {
		LAST_UPDATE("LAST_UPDATE"), TABLE_NAME("TABLE_NAME");

		private String value;


		COLUMN(String value) {
			this.value = value;
		}


		public String getValue() {
			return value;
		}
	}
}
