package de.fmk.kicknrush.db.table;

import de.fmk.kicknrush.helper.TimeUtils;
import de.fmk.kicknrush.models.pojo.Group;
import de.fmk.kicknrush.models.pojo.Match;
import de.fmk.kicknrush.models.pojo.Team;
import org.h2.api.TimestampWithTimeZone;
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
public class MatchTable extends AbstractTable<Integer, Match> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MatchTable.class);

	public static final String NAME = "MATCH";


	public MatchTable() {
		super(NAME);
	}


	@Override
	public boolean merge(Connection connection, Match match) {
		if (match == null)
			throw new IllegalArgumentException("The match object must not be null.");

		try (PreparedStatement statement = connection.prepareStatement(getMergeQuery(COLUMN.values().length))) {
			statement.setInt(1, match.getId());
			statement.setInt(2, match.getGroup().getGroupID());
			statement.setObject(3, TimeUtils.createTimestamp(match.getKickOff()));
			statement.setBoolean(4, match.isFinished());
			statement.setInt(5, match.getHomeTeam().getTeamId());
			statement.setInt(6, match.getGuestTeam().getTeamId());

			if (1 == statement.executeUpdate()) {
				LOGGER.info("The match with id '{}' has been updated.", match.getId());
				return true;
			}
		}
		catch (SQLException sqlex) {
			LOGGER.error("Could not update the match with id '{}'.", match.getId());
		}

		return false;
	}


	@Override
	public void create(Connection connection) throws SQLException {
		final Column[] columns;

		if (connection == null || connection.isClosed())
			throw new IllegalStateException(NO_CONNECTION);

		columns = new Column[] {
				new Column().name(COLUMN.MATCH_ID.getValue()).type(TYPE.INTEGER).constraint(CONSTRAINT.PRIMARY_KEY),
				new Column().name(COLUMN.GROUP_ID.getValue()).type(TYPE.INTEGER).constraint(CONSTRAINT.NOT_NULL),
				new Column().name(COLUMN.KICKOFF.getValue()).type(TYPE.TIMESTAMP),
				new Column().name(COLUMN.MATCH_OVER.getValue()).type(TYPE.BOOLEAN).constraint(CONSTRAINT.DEFAULT_FALSE),
				new Column().name(COLUMN.TEAM_HOME.getValue()).type(TYPE.INTEGER).constraint(CONSTRAINT.NOT_NULL),
				new Column().name(COLUMN.TEAM_GUEST.getValue()).type(TYPE.INTEGER).constraint(CONSTRAINT.NOT_NULL)
		};

		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(getCreationQuery(columns));
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while creating the matches table.", sqlex);
		}
	}


	@Override
	public List<Match> selectAll(Connection connection) {
		final List<Match> matches;

		matches = new ArrayList<>();

		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery(selectAllQuery())) {
				while (rs.next()) {
					final Group group;
					final Match match;
					final Team  teamGuest;
					final Team  teamHome;

					group = new Group();
					group.setGroupID(rs.getInt(COLUMN.GROUP_ID.getValue()));
					group.setGroupName(rs.getString(GroupTable.COLUMN.GROUP_NAME.getValue()));
					group.setGroupOrderID(rs.getInt(GroupTable.COLUMN.GROUP_ORDER_ID.getValue()));
					group.setYear(rs.getInt(GroupTable.COLUMN.YEAR.getValue()));

					teamHome = new Team();
					teamHome.setTeamId(rs.getInt(COLUMN.TEAM_HOME.getValue()));
					teamHome.setTeamName(rs.getString("T1_NAME"));
					teamHome.setTeamIconUrl(rs.getString("T1_ICON"));
					teamHome.setTeamIconUrlSmall(rs.getString("T1_ICON_SMALL"));

					teamGuest = new Team();
					teamGuest.setTeamId(rs.getInt(COLUMN.TEAM_GUEST.getValue()));
					teamGuest.setTeamName(rs.getString("T2_NAME"));
					teamGuest.setTeamIconUrl(rs.getString("T2_ICON"));
					teamGuest.setTeamIconUrlSmall(rs.getString("T2_ICON_SMALL"));

					match = new Match();
					match.setId(rs.getInt(COLUMN.MATCH_ID.getValue()));
					match.setKickOff(TimeUtils.convertTimestamp((TimestampWithTimeZone) rs.getObject(COLUMN.KICKOFF.getValue())));
					match.setFinished(rs.getBoolean(COLUMN.MATCH_OVER.getValue()));
					match.setGroup(group);
					match.setHomeTeam(teamHome);
					match.setGuestTeam(teamGuest);

				  matches.add(match);
				}
			}
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while reading the matches table.", sqlex);
		}

		return matches;
	}


	private String selectAllQuery() {
		return "SELECT m.*,"                                                                             +
		       "g.GROUP_NAME, g.GROUP_ORDER_ID, g.YEAR, "                                                +
		       "t1.TEAM_NAME AS T1_NAME, t1.TEAM_ICON AS T1_ICON, t1.TEAM_ICON_SMALL AS T1_ICON_SMALL, " +
		       "t2.TEAM_NAME AS T2_NAME, t2.TEAM_ICON AS T2_ICON, t2.TEAM_ICON_SMALL AS T2_ICON_SMALL "  +
		       "FROM " + NAME + " AS m "                                                                 +
		       "JOIN " + GroupTable.NAME + " AS g "                                                      +
		       "ON m.GROUP_ID = g.GROUP_ID "                                                             +
		       "JOIN " + TeamTable.NAME + " AS t1 "                                                      +
		       "ON m.TEAM_HOME = t1.TEAM_ID "                                                            +
		       "JOIN " + TeamTable.NAME + " AS t2 "                                                      +
		       "ON m.TEAM_GUEST = t2.TEAM_ID";
	}


	private enum COLUMN {
		GROUP_ID("GROUP_ID"),
		KICKOFF("KICKOFF"),
		MATCH_ID("MATCH_ID"),
		MATCH_OVER("MATCH_OVER"),
		TEAM_GUEST("TEAM_GUEST"),
		TEAM_HOME("TEAM_HOME");

		private String value;


		COLUMN(String value) {
			this.value = value;
		}


		public String getValue() {
			return value;
		}
	}
}
