package de.fmk.kicknrush.db.table;

import lombok.Getter;


/**
 * @author FabianK
 */
@Getter
public class Column {
	private CONSTRAINT constraint;
	private String     name;
	private TYPE       type;


	Column() {
		constraint = CONSTRAINT.NONE;
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

		this.type = type;

		return this;
	}


	Column constraint(CONSTRAINT constraint) {
		if (this.constraint != null)
			throw new IllegalStateException("The constraint is already set.");

		this.constraint = constraint;

		return this;
	}


	String toString(boolean multiplePrimaryKeys) {
		if (!multiplePrimaryKeys)
			return toString();

		if (name == null || type == null)
			throw new IllegalStateException("Name or type is missing.");

		return name.concat(" ").concat(type.getValue());
	}


	@Override
	public String toString() {
		if (name == null || type == null)
			throw new IllegalStateException("Name or type is missing.");

		return name.concat(" ").concat(type.getValue()).concat(" ").concat(constraint.getValue());
	}
}
