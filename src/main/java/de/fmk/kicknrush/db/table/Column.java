package de.fmk.kicknrush.db.table;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * @author FabianK
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Column {
	private String     name;
	private TYPE       type;
	private CONSTRAINT constraint;


	public String multipleKeyString() {
		return name.concat(" ").concat(type.getValue());
	}


	public String singleKeyString() {
		if (CONSTRAINT.NONE == constraint)
			return name.concat(" ").concat(type.getValue());

		return name.concat(" ").concat(type.getValue()).concat(" ").concat(constraint.getValue());
	}
}
