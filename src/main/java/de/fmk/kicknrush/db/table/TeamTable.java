package de.fmk.kicknrush.db.table;

import de.fmk.kicknrush.models.pojo.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


/**
 * @author FabianK
 */
public class TeamTable extends AbstractTable<Integer, Team> {
	private static final Logger LOGGER = LoggerFactory.getLogger(TeamTable.class);

	public static final String TABLE_NAME = "TEAM";

	/* Column names */
	static final String ID         = "TEAM_ID";
	static final String NAME       = "TEAM_NAME";
	static final String ICON       = "TEAM_ICON";
	static final String ICON_SMALL = "TEAM_ICON_SMALL";


	public TeamTable() {
		super(TABLE_NAME);

		addColumn(new Column(ID, TYPE.INTEGER, CONSTRAINT.PRIMARY_KEY));
		addColumn(new Column(NAME, TYPE.VARCHAR255, CONSTRAINT.NOT_NULL));
		addColumn(new Column(ICON, TYPE.VARCHAR255, CONSTRAINT.NOT_NULL));
		addColumn(new Column(ICON_SMALL, TYPE.VARCHAR255, CONSTRAINT.NOT_NULL));
	}


	@Override
	public boolean merge(Connection connection, Team team) {
		if (team == null)
			throw new IllegalArgumentException("The team object must not be null.");

		try (PreparedStatement statement = connection.prepareStatement(getMergeQuery())) {
			statement.setInt(1, team.getTeamId());
			statement.setString(2, team.getTeamName());
			statement.setString(3, team.getTeamIconUrlSmall());
			statement.setString(4, team.getTeamIconUrl());

			if (1 == statement.executeUpdate()) {
				LOGGER.info("The team '{}' has been updated.", team.getTeamName());
				return true;
			}
		}
		catch (SQLException sqlex) {
			LOGGER.error("Could not update the team '{}'.", team.getTeamName(), sqlex);
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
			LOGGER.error("An error occurred while creating the teams table.", sqlex);
		}
	}


	@Override
	public List<Team> selectAll(Connection connection) {
		final List<Team> teams;

		teams = new ArrayList<>();

		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery(getSelectAllQuery())) {
				while (rs.next())
					teams.add(new Team(rs.getInt(ID),
					                   rs.getString(ICON),
					                   rs.getString(ICON_SMALL),
					                   rs.getString(NAME)));
			}
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while reading the teams table.", sqlex);
		}

		return teams;
	}
}
