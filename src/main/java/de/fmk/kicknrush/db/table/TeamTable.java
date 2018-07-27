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

	public static final String NAME = "TEAM";


	public TeamTable() {
		super(NAME);
	}


	@Override
	public boolean merge(Connection connection, Team team) {
		if (team == null)
			throw new IllegalArgumentException("The team object must not be null.");

		try (PreparedStatement statement = connection.prepareStatement(getMergeQuery(COLUMN.values().length))) {
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
		final Column[] columns;

		columns = new Column[] {
				new Column().name(COLUMN.TEAM_ID.getValue()).type(TYPE.INTEGER).constraint(CONSTRAINT.PRIMARY_KEY),
				new Column().name(COLUMN.TEAM_NAME.getValue()).type(TYPE.VARCHAR255).constraint(CONSTRAINT.NOT_NULL),
				new Column().name(COLUMN.ICON_SMALL.getValue()).type(TYPE.VARCHAR255).constraint(CONSTRAINT.NOT_NULL),
				new Column().name(COLUMN.ICON.getValue()).type(TYPE.VARCHAR255).constraint(CONSTRAINT.NOT_NULL)
		};

		if (connection == null || connection.isClosed())
			throw new IllegalStateException(NO_CONNECTION);

		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(getCreationQuery(columns));
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
					teams.add(new Team(rs.getInt(COLUMN.TEAM_ID.getValue()),
					                   rs.getString(COLUMN.ICON.getValue()),
					                   rs.getString(COLUMN.ICON_SMALL.getValue()),
					                   rs.getString(COLUMN.TEAM_NAME.getValue())));
			}
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while reading the teams table.", sqlex);
		}

		return teams;
	}


	private enum COLUMN {
		ICON("TEAM_ICON"), ICON_SMALL("TEAM_ICON_SMALL"), TEAM_ID("TEAM_ID"), TEAM_NAME("TEAM_NAME");

		private String value;


		COLUMN(String value) {
			this.value = value;
		}


		public String getValue() {
			return value;
		}
	}
}
