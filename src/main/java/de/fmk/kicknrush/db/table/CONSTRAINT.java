package de.fmk.kicknrush.db.table;

/**
 * @author FabianK
 */
public enum CONSTRAINT
{
	NOT_NULL("NOT NULL"), PRIMARY_KEY("PRIMARY KEY"), UNIQUE("UNIQUE");


	private String value;


	CONSTRAINT(String value) {
		this.value = value;
	}


	public String getValue() {
		return value;
	}
}
