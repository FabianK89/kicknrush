package de.fmk.kicknrush.db.table;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author FabianK
 */
public abstract class AbstractTable<K, V> implements ITable<K, V> {
	static final String NO_CONNECTION = "The connection to the database must be established first.";

	protected final Map<String, Column> columns;
	protected final Map<String, Column> primaryKeys;

	private final String tableName;


	AbstractTable(String tableName) {
		this.tableName = tableName;

		columns     = new LinkedHashMap<>();
		primaryKeys = new LinkedHashMap<>();
	}


	@Override
	public String getName() {
		return tableName;
	}


	@Override
	public void addColumn(Column column) {
		if (column == null)
			throw new IllegalArgumentException("The column parameter must not be null.");

		if (column.getConstraint() == CONSTRAINT.PRIMARY_KEY)
			primaryKeys.put(column.getName(), column);

		columns.put(column.getName(), column);
	}


	String getCreationQuery() {
		final StringBuilder builder;
		final StringBuilder primaryKeyBuilder;

		if (columns.isEmpty())
			throw new IllegalArgumentException("There must be columns for the table.");

		if (primaryKeys.size() > 1)
			primaryKeyBuilder = new StringBuilder(" PRIMARY KEY(");
		else
			primaryKeyBuilder = null;

		builder = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
		builder.append(tableName);
		builder.append("(");

		for (Iterator<Column> it = columns.values().iterator(); it.hasNext();) {
			final Column column = it.next();

			if (primaryKeyBuilder == null || column.getConstraint() != CONSTRAINT.PRIMARY_KEY) {
				builder.append(column.singleKeyString());
			}
			else {
				builder.append(column.multipleKeyString());

				if (!primaryKeyBuilder.toString().endsWith("("))
					primaryKeyBuilder.append(", ");

				primaryKeyBuilder.append(column.getName());
			}

			if (it.hasNext())
				builder.append(", ");
		}

		if (primaryKeyBuilder != null) {
			primaryKeyBuilder.append(")");
			builder.append(primaryKeyBuilder.toString());
		}

		builder.append(");");

		return builder.toString();
	}


	String getMergeQuery() {
		final StringBuilder keyBuilder;
		final StringBuilder queryBuilder;
		final StringBuilder valuesBuilder;

		if (columns.isEmpty())
			throw new IllegalArgumentException("No columns available.");

		queryBuilder  = new StringBuilder("MERGE INTO ");
		keyBuilder    = new StringBuilder(") KEY (");
		valuesBuilder = new StringBuilder(") VALUES (");

		queryBuilder.append(tableName).append("(");

		for (Iterator<String> it = primaryKeys.keySet().iterator(); it.hasNext();) {
			keyBuilder.append(it.next());

			if (it.hasNext())
				keyBuilder.append(", ");
		}

		for (Iterator<String> it = columns.keySet().iterator(); it.hasNext();) {
			queryBuilder.append(it.next());
			valuesBuilder.append("?");

			if (it.hasNext()) {
				queryBuilder.append(", ");
				valuesBuilder.append(", ");
			}
		}

		valuesBuilder.append(");");
		keyBuilder.append(valuesBuilder.toString());
		queryBuilder.append(keyBuilder.toString());

		return queryBuilder.toString();
	}


	String getSelectAllQuery() {
		final StringBuilder builder;

		builder = new StringBuilder("SELECT * FROM ");
		builder.append(tableName);

		return builder.toString();
	}
}
