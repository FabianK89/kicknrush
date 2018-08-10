package de.fmk.kicknrush.db.table;

import de.fmk.kicknrush.models.pojo.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


/**
 * @author FabianK
 */
public class GroupTable extends AbstractTable<Integer, Group> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GroupTable.class);

	public static final String NAME = "GROUPS";


	public GroupTable() {
		super(NAME);
	}


	@Override
	public boolean merge(Connection connection, Group value) {
		return false;
	}


	@Override
	public void create(Connection connection) throws SQLException {
		final Column[] columns;

		if (connection == null || connection.isClosed())
			throw new IllegalStateException(NO_CONNECTION);

		columns = new Column[] {
				new Column().name(COLUMN.GROUP_ID.getValue()).type(TYPE.INTEGER).constraint(CONSTRAINT.PRIMARY_KEY),
				new Column().name(COLUMN.GROUP_NAME.getValue()).type(TYPE.VARCHAR255).constraint(CONSTRAINT.NOT_NULL),
				new Column().name(COLUMN.GROUP_ORDER_ID.getValue()).type(TYPE.INTEGER).constraint(CONSTRAINT.NOT_NULL),
				new Column().name(COLUMN.YEAR.getValue()).type(TYPE.INTEGER).constraint(CONSTRAINT.NOT_NULL)
		};

		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(getCreationQuery(columns));
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while creating the groups table.", sqlex);
		}
	}


	@Override
	public List<Group> selectAll(Connection connection) {
		return null;
	}


	enum COLUMN {
		GROUP_ID("GROUP_ID"), GROUP_NAME("GROUP_NAME"), GROUP_ORDER_ID("GROUP_ORDER_ID"), YEAR("YEAR");

		private String value;


		COLUMN(String value) {
			this.value = value;
		}


		public String getValue() {
			return value;
		}
	}
}
