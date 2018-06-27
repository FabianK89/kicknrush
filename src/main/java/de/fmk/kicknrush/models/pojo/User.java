package de.fmk.kicknrush.models.pojo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
	private String id;

	private boolean isAdmin;
	private String  password;
	private String  salt;
	private String  username;
	private String  sessionID;
}
