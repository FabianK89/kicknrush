package de.fmk.kicknrush.db.table;

/**
 * @author FabianK
 */
public class Column {
	private String constraint;
	private String name;
	private String type;


	Column() {
		constraint = null;
		name       = null;
		type       = null;
	}


	public Column name(String name) {
		if (this.name != null)
			throw new IllegalStateException("The name is already set.");

		this.name = name;

		return this;
	}


	Column type(TYPE type) {
		if (this.type != null)
			throw new IllegalStateException("The type is already set.");

		this.type = type.getValue();

		return this;
	}


	Column constraint(CONSTRAINT constraint) {
		if (this.constraint != null)
			throw new IllegalStateException("The constraint is already set.");

		this.constraint = constraint.getValue();

		return this;
	}


	@Override
	public String toString() {
		if (name == null || type == null)
			throw new IllegalStateException("Name or type is missing.");

		return name.concat(" ").concat(type).concat(" ").concat(constraint);
	}
}
