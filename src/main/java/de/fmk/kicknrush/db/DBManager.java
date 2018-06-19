package de.fmk.kicknrush.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(DBManager.class);

	private static DBManager instance;

	private Connection connection;


	private DBManager() throws ClassNotFoundException {
		Class.forName("org.h2.Driver");
	}


	public static DBManager getInstance() throws ClassNotFoundException {
		if (instance == null)
			instance = new DBManager();

		return instance;
	}


	public void connect() throws SQLException {
		if (connection == null)
		{
			LOGGER.info("Connect to database...");
			connection = DriverManager.getConnection("jdbc:h2:~/.kicknrush/data", "admin", "1234");
			LOGGER.info("Connection to database is established.");
		}
	}


	public void closeConnection() throws SQLException
	{
		LOGGER.info("Close database connection...");

		try {
			if (connection != null && !connection.isClosed())
				connection.close();

			connection = null;
		}
		catch (SQLException sqlex) {
			LOGGER.error(sqlex.getMessage(), sqlex);
		}
		finally {
			if (connection != null) {
				connection.close();
				connection = null;
			}

			LOGGER.info("Connection to the database was successfully closed.");
		}
	}
}
