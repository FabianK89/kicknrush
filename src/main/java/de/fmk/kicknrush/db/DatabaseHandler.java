package de.fmk.kicknrush.db;

import de.fmk.kicknrush.db.table.GroupTable;
import de.fmk.kicknrush.db.table.MatchTable;
import de.fmk.kicknrush.db.table.TeamTable;
import de.fmk.kicknrush.db.table.UpdateTable;
import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.SettingCache;
import de.fmk.kicknrush.helper.cache.SettingCacheKey;
import de.fmk.kicknrush.models.pojo.Group;
import de.fmk.kicknrush.models.pojo.Match;
import de.fmk.kicknrush.models.pojo.Team;
import de.fmk.kicknrush.models.pojo.Update;
import de.fmk.kicknrush.models.pojo.User;
import org.apache.commons.dbcp2.BasicDataSource;
import org.h2.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.UUID;


public class DatabaseHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHandler.class);

	private static final String ADMIN           = "admin";
	private static final String NO_CONNECTION   = "The connection to the database must be established first.";
	private static final String SELECT_ALL_FROM = "SELECT * FROM ";
	private static final String WHERE           = " WHERE ";

	private final BasicDataSource connectionPool;
	private final GroupTable      groupTable;
	private final MatchTable      matchTable;
	private final TeamTable       teamTable;
	private final UpdateTable     updateTable;


	public DatabaseHandler(Properties properties) {
		connectionPool = new BasicDataSource();
		groupTable     = new GroupTable();
		matchTable     = new MatchTable();
		teamTable      = new TeamTable();
		updateTable    = new UpdateTable();

		if (properties == null)
			throw new IllegalArgumentException("Properties must not be null.");

		initConnectionPool(properties);
	}


	public void closeConnections() throws SQLException {
		connectionPool.close();
	}


	public void createInitialTables() {
		final List<String> existingTables;

		try (Connection connection = connectionPool.getConnection()) {
			existingTables = getExistingTables(connection);

			if (!existingTables.contains(updateTable.getName()))
				updateTable.create(connection);

			if (!existingTables.contains(DBConstants.TBL_NAME_USER))
				createUserTable(connection);

			if (!existingTables.contains(DBConstants.TBL_NAME_SETTINGS))
				createSettingsTable(connection);

			if (!existingTables.contains(teamTable.getName()))
				teamTable.create(connection);

			if (!existingTables.contains(groupTable.getName()))
				groupTable.create(connection);

			if (!existingTables.contains(matchTable.getName()))
				matchTable.create(connection);
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while connecting to the database.", sqlex);
		}
	}


	public boolean mergeGroup(final Group group) {
		try (Connection connection = connectionPool.getConnection()) {
			return groupTable.merge(connection, group);
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while updating the group '{}'.", group.getGroupName(), sqlex);
			return false;
		}
	}


	public boolean mergeMatch(final Match match) {
		try (Connection connection = connectionPool.getConnection()) {
			return matchTable.merge(connection, match);
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while updating the match '{}'.", match.getId(), sqlex);
			return false;
		}
	}


	public boolean mergeTeam(final Team team) {
		try (Connection connection = connectionPool.getConnection()) {
			return teamTable.merge(connection, team);
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while updating the team '{}'.", team.getTeamName(), sqlex);
			return false;
		}
	}


	public List<Group> selectAllGroups() {
		try (Connection connection = connectionPool.getConnection()) {
			return groupTable.selectAll(connection);
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while selecting all entries of the group table.", sqlex);
			return Collections.emptyList();
		}
	}


	public List<Team> selectAllTeams() {
		try (Connection connection = connectionPool.getConnection()) {
			return teamTable.selectAll(connection);
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while selecting all entries of the teams table.", sqlex);
			return Collections.emptyList();
		}
	}


	public List<Update> selectAllUpdates() {
		try (Connection connection = connectionPool.getConnection()) {
			return updateTable.selectAll(connection);
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while selecting all entries of the updates table.", sqlex);
			return Collections.emptyList();
		}
	}


	public List<Match> selectMatchesForGroup(final Group group) {
		final List<Match> matches;

		if (group == null)
			throw new IllegalArgumentException("The group parameter must not be null.");

		try (Connection connection = connectionPool.getConnection()) {
			matches = matchTable.selectForGroup(connection, group);
			matches.sort(Comparator.comparing(Match::getKickOff));

			return matches;
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while selecting matches for the group with id '{}'.", group.getGroupID(), sqlex);
			return Collections.emptyList();
		}
	}


	public boolean storeUpdate(final Update update) {
		try (Connection connection = connectionPool.getConnection()) {
			return updateTable.merge(connection, update);
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while storing the update of table '{}'.", update.getTableName(), sqlex);
			return false;
		}
	}


	public void loadSettings(final CacheProvider cacheProvider) throws SQLException {
		final StringBuilder queryBuilder;

		try (Connection connection = connectionPool.getConnection())
		{
			queryBuilder = new StringBuilder();
			queryBuilder.append(SELECT_ALL_FROM).append(DBConstants.TBL_NAME_SETTINGS).append(";");

			try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString()))
			{
				try (ResultSet result = statement.executeQuery())
				{
					while (result.next())
					{
						final SettingCacheKey key;

						key = SettingCacheKey.getByKey(result.getString(DBConstants.COL_NAME_KEY));

						cacheProvider.getSettingCache().parseAndPutStringValue(key, result.getString(DBConstants.COL_NAME_VALUE));
					}
				}
			}
			catch (SQLException sqlex)
			{
				LOGGER.error("An error occurred while reading the settings from the database.", sqlex);
			}
		}
	}


	public User loginUser(final String username, final String password) throws SQLException {
		final StringBuilder queryBuilder;

		if (username == null || username.isEmpty() || password == null || password.isEmpty())
			return null;

		try (Connection connection = connectionPool.getConnection())
		{
			queryBuilder = new StringBuilder();
			queryBuilder.append(SELECT_ALL_FROM).append(DBConstants.TBL_NAME_USER)
			            .append(WHERE).append(DBConstants.COL_NAME_USERNAME)
			            .append("=? AND ").append(DBConstants.COL_NAME_PWD).append("=?;");

			try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString()))
			{
				statement.setString(1, username);
				statement.setString(2, password);

				try (ResultSet result = statement.executeQuery())
				{
					if (result.next()) {
						final Object id;
						final User   user;

						user = new User();
						id   = result.getObject(DBConstants.COL_NAME_ID);

						if (id instanceof UUID)
							user.setId(((UUID) id));
						else
							return null;

						user.setUsername(result.getString(DBConstants.COL_NAME_USERNAME));
						user.setPassword(result.getString(DBConstants.COL_NAME_PWD));
						user.setAdmin(result.getBoolean(DBConstants.COL_NAME_IS_ADMIN));

						return user;
					}
				}
			}
		}

		return null;
	}


	public String readSalt(final String username) throws SQLException {
		final StringBuilder queryBuilder;

		if (username == null)
			throw new IllegalArgumentException("The username must not be null.");

		queryBuilder = new StringBuilder();
		queryBuilder.append(SELECT_ALL_FROM).append(DBConstants.TBL_NAME_USER)
		            .append(WHERE).append(DBConstants.COL_NAME_USERNAME).append("=?;");

		try (Connection connection = connectionPool.getConnection()) {
			try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
				statement.setString(1, username);

				try (ResultSet rs = statement.executeQuery()) {
					if (rs.next())
						return rs.getString(DBConstants.COL_NAME_SALT);
				}
			}
			catch (SQLException sqlex) {
				LOGGER.error("An error occurred while reading the salt value for user '{}'.", username, sqlex);
			}
		}

		return null;
	}


	public void saveSettings(final SettingCache cachedSettings) throws SQLException {
		final StringBuilder queryBuilder;

		if (cachedSettings == null || cachedSettings.isEmpty())
			return;

		queryBuilder = new StringBuilder();
		queryBuilder.append("MERGE INTO ").append(DBConstants.TBL_NAME_SETTINGS)
		            .append(" KEY(").append(DBConstants.COL_NAME_KEY).append(") VALUES(?,?);");

		try (Connection connection = connectionPool.getConnection()) {
			cachedSettings.forEachKey(key -> {
				try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
					statement.setString(1, key.getKey());
					statement.setString(2, cachedSettings.getValueAsString(key));

					if (1 != statement.executeUpdate())
						LOGGER.warn("The setting '{}' could not have been saved.", key.getKey());
				}
				catch (SQLException sqlex) {
					LOGGER.error("An error occurred while saving the setting '{}'.", key.getKey(), sqlex);
				}
			});
		}
	}


	public void updateUser(final User user) throws SQLException {
		final StringBuilder queryBuilder;

		if (user == null)
			throw new IllegalArgumentException("The user must not be null.");

		try (Connection connection = connectionPool.getConnection()) {
			queryBuilder = new StringBuilder();
			queryBuilder.append("MERGE INTO ").append(DBConstants.TBL_NAME_USER).append(" VALUES(?,?,?,?,?);");

			try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
				statement.setObject(1, user.getId());
				statement.setString(2, user.getUsername());
				statement.setString(3, user.getPassword());
				statement.setString(4, user.getSalt());
				statement.setBoolean(5, user.isAdmin());

				if (1 != statement.executeUpdate())
					LOGGER.warn("Could not update the user with id '{}'.", user.getId());
			}
			catch (SQLException sqlex) {
				LOGGER.error("Could not update the user with id '{}'.", user.getId(), sqlex);
			}
		}
	}


	private void createSettingsTable(final Connection connection) throws SQLException {
		final StringBuilder queryBuilder;

		if (connection == null || connection.isClosed())
			throw new IllegalStateException(NO_CONNECTION);

		queryBuilder = new StringBuilder();

		try (Statement statement = connection.createStatement()) {
			queryBuilder.append("CREATE TABLE IF NOT EXISTS ")
			            .append(DBConstants.TBL_NAME_SETTINGS)
			            .append("(").append(DBConstants.COL_NAME_KEY).append(" VARCHAR(255) PRIMARY KEY, ")
			            .append(DBConstants.COL_NAME_VALUE).append(" VARCHAR(255));");

			statement.executeUpdate(queryBuilder.toString());
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while creating the settings table.", sqlex);
		}
	}


	private void createUpdatesTable(final Connection connection) throws SQLException {
		final StringBuilder queryBuilder;

		if (connection == null || connection.isClosed())
			throw new IllegalStateException(NO_CONNECTION);

		queryBuilder = new StringBuilder();

		try (Statement statement = connection.createStatement()) {
			queryBuilder.append("CREATE TABLE IF NOT EXISTS ")
			            .append(DBConstants.TBL_NAME_UPDATES)
			            .append("(").append(DBConstants.COL_NAME_TABLE_NAME).append(" VARCHAR(255) PRIMARY KEY, ")
			            .append(DBConstants.COL_NAME_LAST_UPDATE).append(" TIMESTAMP WITH TIME ZONE NOT NULL);");

			statement.executeUpdate(queryBuilder.toString());
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while creating the updates table.", sqlex);
		}
	}


	private void createUserTable(final Connection connection) throws SQLException {
		final StringBuilder queryBuilder;

		if (connection == null || connection.isClosed())
			throw new IllegalStateException(NO_CONNECTION);

		queryBuilder = new StringBuilder();

		try (Statement statement = connection.createStatement()) {
			queryBuilder.append("CREATE TABLE IF NOT EXISTS ")
			            .append(DBConstants.TBL_NAME_USER)
			            .append("(").append(DBConstants.COL_NAME_ID).append(" UUID PRIMARY KEY, ")
			            .append(DBConstants.COL_NAME_USERNAME).append(" VARCHAR(255) UNIQUE, ")
			            .append(DBConstants.COL_NAME_PWD).append(" VARCHAR(255), ")
			            .append(DBConstants.COL_NAME_SALT).append(" VARCHAR(255), ")
			            .append(DBConstants.COL_NAME_IS_ADMIN).append(" BOOLEAN);");

			statement.executeUpdate(queryBuilder.toString());
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while creating the user table.", sqlex);
		}
	}



	private  List<String> getExistingTables(final Connection connection) throws SQLException {
		final List<String> existingTables = new ArrayList<>();

		if (connection == null || connection.isClosed())
			throw new IllegalStateException(NO_CONNECTION);

		try (Statement statement = connection.createStatement()) {
			try (ResultSet rs = statement.executeQuery("SELECT * FROM INFORMATION_SCHEMA.TABLES")) {
				while (rs.next())
					existingTables.add(rs.getString("TABLE_NAME"));
			}
			catch (SQLException sqlex) {
				LOGGER.error("An error occurred while searching for existing tables.", sqlex);
			}
		}

		return existingTables;
	}


	private void initConnectionPool(final Properties properties) {
		connectionPool.setDriver(new Driver());
		connectionPool.setUrl(properties.getProperty("db.url"));
		connectionPool.setUsername(properties.getProperty("db.user"));
		connectionPool.setPassword(properties.getProperty("db.password"));
		connectionPool.setTestWhileIdle(true);
	}


	public List<String> getExistingTables() {
		try (Connection connection = connectionPool.getConnection()) {
			return getExistingTables(connection);
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while connecting to the database.", sqlex);
			return Collections.emptyList();
		}
	}
}
