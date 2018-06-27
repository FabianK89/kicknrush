package de.fmk.kicknrush.db;

import de.fmk.kicknrush.helper.CacheProvider;
import de.fmk.kicknrush.helper.SettingCacheKey;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class DatabaseHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHandler.class);

	private static final String ADMIN           = "admin";
	private static final String NO_CONNECTION   = "The connection to the database must be established first.";
	private static final String SELECT_ALL_FROM = "SELECT * FROM ";
	private static final String WHERE           = " WHERE ";

	private final BasicDataSource connectionPool;


	public DatabaseHandler() {
		connectionPool = new BasicDataSource();
		connectionPool.setDriver(new Driver());
		connectionPool.setUrl("jdbc:h2:~/.kicknrush/data");
		connectionPool.setUsername("dbadmin");
		connectionPool.setPassword("admin1234");
		connectionPool.setTestWhileIdle(true);
	}


	public void createInitialTables() {
		final List<String> existingTables;

		try (Connection connection = connectionPool.getConnection()) {
			existingTables = getExistingTables(connection);

			if (!existingTables.contains(DBConstants.TBL_NAME_USER))
				createUserTable(connection);

			if (!existingTables.contains(DBConstants.TBL_NAME_SETTINGS))
				createSettingsTable(connection);
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while connecting to the database.", sqlex);
		}
	}


	public void updateUser(final User user) throws SQLException {
		final StringBuilder queryBuilder;

		try (Connection connection = connectionPool.getConnection()) {
			queryBuilder = new StringBuilder();
			queryBuilder.append("MERGE INTO ").append(DBConstants.TBL_NAME_USER).append(" VALUES(?,?,?,?,?);");

			try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
				statement.setObject(1, UUID.fromString(user.getId()));
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


	private List<String> getExistingTables(final Connection connection) throws SQLException {
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


	public void saveSettings(final Map<SettingCacheKey, String> cachedSettings) throws SQLException {
		final StringBuilder queryBuilder;

		if (cachedSettings == null || cachedSettings.isEmpty())
			return;

		queryBuilder = new StringBuilder();
		queryBuilder.append("MERGE INTO ").append(DBConstants.TBL_NAME_SETTINGS)
		            .append(" KEY(").append(DBConstants.COL_NAME_KEY).append(") VALUES(?,?);");

		try (Connection connection = connectionPool.getConnection()) {
			cachedSettings.forEach((key, value) -> {
				try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
					statement.setString(1, key.getKey());
					statement.setString(2, value);

					if (1 != statement.executeUpdate())
						LOGGER.warn("The setting '{}' could not have been saved.", key.getKey());
				}
				catch (SQLException sqlex) {
					LOGGER.error("An error occurred while saving the setting '{}'.", key.getKey(), sqlex);
				}
			});
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
							user.setId(((UUID) id).toString());
						else
							return null;

						user.setUsername(result.getString(DBConstants.COL_NAME_USERNAME));
						user.setPassword(result.getString(DBConstants.COL_NAME_PWD));

						return user;
					}
				}
			}
		}

		return null;
	}


	private boolean adminAccountExists(final Connection connection) throws SQLException {
		final StringBuilder queryBuilder;

		if (connection == null || connection.isClosed())
			throw new IllegalStateException(NO_CONNECTION);

		queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT ").append(DBConstants.COL_NAME_USERNAME)
		            .append(" FROM ").append(DBConstants.TBL_NAME_USER)
		            .append(WHERE).append(DBConstants.COL_NAME_USERNAME).append("=?;");

		try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
			statement.setString(1, ADMIN);

			try (ResultSet result = statement.executeQuery()) {
				return result.next();
			}
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while searching the admin account in the user table.", sqlex);
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

						cacheProvider.putSetting(key, result.getString(DBConstants.COL_NAME_VALUE));
					}
				}
			}
			catch (SQLException sqlex)
			{
				LOGGER.error("An error occurred while reading the settings from the database.", sqlex);
			}
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


	private void fillPreparedStatement(final PreparedStatement statement, final int index, final Object value)
			throws SQLException {
		if (value instanceof String)
			statement.setString(index, (String) value);
		else if (value instanceof UUID)
			statement.setObject(index, value);
	}


	private void insertIntoTable(final Connection connection, final String tableName, final Object... values)
			throws SQLException {
		final int           result;
		final StringBuilder queryBuilder;

		if (connection == null || connection.isClosed())
			throw new IllegalStateException(NO_CONNECTION);

		LOGGER.info("Insert data into table {}.", tableName);

		queryBuilder = new StringBuilder();
		queryBuilder.append("INSERT INTO ").append(tableName).append(" VALUES(");

		for (int i = 0; i < values.length; i++) {
			queryBuilder.append("?");

			if (i < values.length - 1)
				queryBuilder.append(", ");
		}

		queryBuilder.append(");");

		try (PreparedStatement statement = connection.prepareStatement(queryBuilder.toString())) {
			for (int i = 1; i <= values.length; i++)
				fillPreparedStatement(statement, i, values[i-1]);

			result = statement.executeUpdate();

			if (result == 1)
				LOGGER.info("Data was successfully inserted into the table {}.", tableName);
		}
		catch (SQLException sqlex) {
			LOGGER.error("An error occurred while inserting data into table {}.", tableName, sqlex);
		}
	}
}
