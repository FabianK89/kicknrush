package de.fmk.kicknrush.db.table;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


/**
 * @author FabianK
 */
public interface ITable<K, V> {
	boolean merge(Connection connection, V value);

	void create(Connection connection) throws SQLException;

	List<V> selectAll(Connection connection);

	String getName();

	void addColumn(Column column);
}
