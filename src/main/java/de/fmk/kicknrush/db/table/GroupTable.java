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

	static final String TABLE_NAME = "GROUPS";

	/* Column names */
	static final String ID       = "GROUP_ID";
	static final String NAME     = "GROUP_NAME";
	static final String ORDER_ID = "GROUP_ORDER_ID";
	static final String YEAR     = "YEAR";


	public GroupTable() {
		super(TABLE_NAME);

		addColumn(new Column(ID, TYPE.INTEGER, CONSTRAINT.PRIMARY_KEY));
		addColumn(new Column(NAME, TYPE.VARCHAR255, CONSTRAINT.NOT_NULL));
		addColumn(new Column(ORDER_ID, TYPE.INTEGER, CONSTRAINT.NOT_NULL));
		addColumn(new Column(YEAR, TYPE.INTEGER, CONSTRAINT.NOT_NULL));
	}


	@Override
	public boolean merge(Connection connection, Group value) {
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
			LOGGER.error("An error occurred while creating the groups table.", sqlex);
		}
	}


	@Override
	public List<Group> selectAll(Connection connection) {
		return null;
	}
}
