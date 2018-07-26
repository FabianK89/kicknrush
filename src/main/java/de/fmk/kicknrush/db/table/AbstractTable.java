package de.fmk.kicknrush.db.table;

/**
 * @author FabianK
 */
public abstract class AbstractTable<K, V> implements ITable<K, V> {
	static final String NO_CONNECTION   = "The connection to the database must be established first.";


	private final String tableName;


	AbstractTable(String tableName) {
		this.tableName = tableName;
	}


	@Override
	public String getName() {
		return tableName;
	}


	String getCreationQuery(final Column[] columns) {
		final StringBuilder builder;

		if (columns == null || columns.length == 0)
			throw new IllegalArgumentException("There must be columns for the table.");

		builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
		builder.append(tableName);
		builder.append("(");

		for (int i = 0; i < columns.length; i++) {
			builder.append(columns[i].toString());

			if (i + 1 < columns.length)
				builder.append(", ");
		}

		builder.append(");");

		return builder.toString();
	}


	String getMergeQuery(final int columns) {
		final StringBuilder builder;

		builder = new StringBuilder("MERGE INTO ");
		builder.append(tableName);
		builder.append(" VALUES(");

		for (int i = 1; i <= columns; i++) {
			builder.append("?");

			if (i < columns)
				builder.append(",");
		}

		builder.append(");");

		return builder.toString();
	}


	String getSelectAllQuery() {
		final StringBuilder builder;

		builder = new StringBuilder("SELECT * FROM ");
		builder.append(tableName);

		return builder.toString();
	}
}
