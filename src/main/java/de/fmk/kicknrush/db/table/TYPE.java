package de.fmk.kicknrush.db.table;

/**
 * @author FabianK
 */
public enum TYPE
{
	BOOLEAN("BOOLEAN"), INTEGER("INTEGER"), TIMESTAMP("TIMESTAMP WITH TIME ZONE"), VARCHAR255("VARCHAR(255)");


	private String value;


	TYPE(String value) {
		this.value = value;
	}


	public String getValue() {
		return value;
	}
}
