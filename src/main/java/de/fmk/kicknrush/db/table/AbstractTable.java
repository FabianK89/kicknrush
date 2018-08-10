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
		final StringBuilder primaryKeyBuilder;

		if (columns == null || columns.length == 0)
			throw new IllegalArgumentException("There must be columns for the table.");

		if (hasMultiplePrimaryKeys(columns))
			primaryKeyBuilder = new StringBuilder(" PRIMARY KEY(");
		else
			primaryKeyBuilder = null;

		builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
		builder.append(tableName);
		builder.append("(");

		for (int i = 0; i < columns.length; i++) {
			final Column column = columns[i];

			if (primaryKeyBuilder == null || column.getConstraint() != CONSTRAINT.PRIMARY_KEY) {
				builder.append(column.toString());
			}
			else {
				builder.append(column.toString(true));

				if (!primaryKeyBuilder.toString().endsWith("("))
					primaryKeyBuilder.append(", ");

				primaryKeyBuilder.append(column.getName());
			}

			if (i + 1 < columns.length)
				builder.append(", ");
		}

		if (primaryKeyBuilder != null) {
			primaryKeyBuilder.append(")");
			builder.append(primaryKeyBuilder.toString());
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


	private boolean hasMultiplePrimaryKeys(final Column[] columns) {
		int primaryKeys = 0;

		for (final Column column : columns) {
			if (column.getConstraint() == CONSTRAINT.PRIMARY_KEY)
				primaryKeys++;
		}

		return primaryKeys > 1;
	}
}
