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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author FabianK
 */
public class MatchTable extends AbstractTable<Integer, Match> {
	private static final Logger LOGGER = LoggerFactory.getLogger(MatchTable.class);

	private static final String G  = "G";
	private static final String T1 = "T1";
	private static final String T2 = "T2";

	public static final String TABLE_NAME = "MATCH";

	/* Column names */
	static final String GROUP   = "GROUP_ID";
	static final String GUEST   = "TEAM_GUEST";
	static final String HOME    = "TEAM_HOME";
	static final String ID      = "MATCH_ID";
	static final String KICKOFF = "KICKOFF";
	static final String OVER    = "MATCH_OVER";


	public MatchTable() {
		super(TABLE_NAME);

		addColumn(new Column(ID, TYPE.INTEGER, CONSTRAINT.PRIMARY_KEY));
		addColumn(new Column(GROUP, TYPE.INTEGER, CONSTRAINT.NOT_NULL));
		addColumn(new Column(KICKOFF, TYPE.TIMESTAMP, CONSTRAINT.NONE));
		addColumn(new Column(OVER, TYPE.BOOLEAN, CONSTRAINT.DEFAULT_FALSE));
		addColumn(new Column(HOME, TYPE.INTEGER, CONSTRAINT.NOT_NULL));
		addColumn(new Column(GUEST, TYPE.INTEGER, CONSTRAINT.NOT_NULL));
	}


	@Override
	public boolean merge(Connection connection, Match match) {
		if (match == null)
			throw new IllegalArgumentException("The match object must not be null.");

		try (PreparedStatement statement = connection.prepareStatement(getMergeQuery())) {
			statement.setInt(1, match.getId());
			statement.setInt(2, match.getGroup().getGroupID());
			statement.setObject(3, TimeUtils.createTimestamp(match.getKickOff(), false));
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
		if (connection == null || connection.isClosed())
			throw new IllegalStateException(NO_CONNECTION);

		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate(getCreationQuery());
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while creating the matches table.", sqlex);
		}
	}


	@Override
	public List<Match> selectAll(Connection connection) {
		final List<Match>         matches;
		final Map<Integer, Group> groups;

		matches = new ArrayList<>();
		groups  = new HashMap<>();

		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery(getSelectAllQuery())) {
				while (rs.next()) {
					final Group group;
					final Match match;

					group = groups.get(rs.getInt(GROUP));
					match = buildMatch(rs, group);

					matches.add(match);

					if (group == null)
						groups.put(match.getGroup().getGroupID(), match.getGroup());
				}
			}
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while reading the matches table.", sqlex);
		}

		return matches;
	}


	public List<Match> selectForGroup(Connection connection, Group group) {
		final List<Match> matches;
		final String      sql;

		matches = new ArrayList<>();

		sql = getSelectAllQuery().concat(" WHERE m.").concat(GROUP).concat("=?;");

		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, group.getGroupID());

			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next())
					matches.add(buildMatch(rs, group));
			}
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while reading the matches table.", sqlex);
		}

		return matches;
	}


	private String aliasColumn(String alias, String columnName) {
		return alias.concat("_").concat(columnName);
	}


	private Match buildMatch(ResultSet rs, Group group) throws SQLException {
		final Group newGroup;
		final Match match;
		final Team  teamGuest;
		final Team  teamHome;

		if (group == null) {
			newGroup = new Group();
			newGroup.setGroupID(rs.getInt(GROUP));
			newGroup.setGroupName(rs.getString(aliasColumn(G, GroupTable.NAME)));
			newGroup.setGroupOrderID(rs.getInt(aliasColumn(G, GroupTable.ORDER_ID)));
			newGroup.setYear(rs.getInt(aliasColumn(G, GroupTable.YEAR)));
		}
		else {
			newGroup = group;
		}

		teamHome = new Team();
		teamHome.setTeamId(rs.getInt(HOME));
		teamHome.setTeamName(rs.getString(aliasColumn(T1, TeamTable.NAME)));
		teamHome.setTeamIconUrl(rs.getString(aliasColumn(T1, TeamTable.ICON)));
		teamHome.setTeamIconUrlSmall(rs.getString(aliasColumn(T1, TeamTable.ICON_SMALL)));

		teamGuest = new Team();
		teamGuest.setTeamId(rs.getInt(GUEST));
		teamGuest.setTeamName(rs.getString(aliasColumn(T2, TeamTable.NAME)));
		teamGuest.setTeamIconUrl(rs.getString(aliasColumn(T2, TeamTable.ICON)));
		teamGuest.setTeamIconUrlSmall(rs.getString(aliasColumn(T2, TeamTable.ICON_SMALL)));

		match = new Match();
		match.setId(rs.getInt(ID));
		match.setKickOff(TimeUtils.convertTimestamp((TimestampWithTimeZone) rs.getObject(KICKOFF)));
		match.setFinished(rs.getBoolean(OVER));
		match.setGroup(newGroup);
		match.setHomeTeam(teamHome);
		match.setGuestTeam(teamGuest);

		if (newGroup.getFirstKickOff() == null || newGroup.getFirstKickOff().isAfter(match.getKickOff()))
			newGroup.setFirstKickOff(match.getKickOff());

		if (newGroup.getLastKickOff() == null || newGroup.getLastKickOff().isBefore(match.getKickOff()))
			newGroup.setLastKickOff(match.getKickOff());

		return match;
	}


	private String columns(String alias, String... columns) {
		StringBuilder s = new StringBuilder();

		for (int i = 0; i < columns.length; i++) {
			s.append(alias).append(".").append(columns[i]).append(" AS ").append(alias).append("_").append(columns[i]);

			if (i + 1 < columns.length)
				s.append(", ");
		}

		return s.toString();
	}


	private String join(String tableName, String alias, String matchCol, String joinCol) {
		return "JOIN " + tableName + " AS " + alias + " ON m." + matchCol + " = " + alias + "." + joinCol + " ";
	}


	@Override
	String getSelectAllQuery() {
		return "SELECT m.*, "                                                    +
		       columns(G, GroupTable.NAME, GroupTable.ORDER_ID, GroupTable.YEAR) + ", " +
		       columns(T1, TeamTable.NAME, TeamTable.ICON, TeamTable.ICON_SMALL) + ", " +
		       columns(T2, TeamTable.NAME, TeamTable.ICON, TeamTable.ICON_SMALL) +
		       " FROM " + getName() + " AS m "                                   +
		       join(GroupTable.TABLE_NAME, G, GROUP, GroupTable.ID)              +
		       join(TeamTable.TABLE_NAME, T1, HOME, TeamTable.ID)                +
		       join(TeamTable.TABLE_NAME, T2, GUEST, TeamTable.ID);
	}
}
