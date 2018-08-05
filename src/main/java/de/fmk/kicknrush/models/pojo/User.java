package de.fmk.kicknrush.models.pojo;

import de.fmk.kicknrush.models.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {
	private boolean isAdmin;
	private String  password;
	private String  salt;
	private String  username;
	private UUID    sessionID;
	private UUID    id;


	/**
	 * Convert the data transfer object to an user object.
	 * @param dto The data transfer object.
	 * @return a user object or <code>null</code>.
	 */
	public static User fromDTO(final UserDTO dto) {
		final User user = new User();

		if (dto == null || dto.getUserID() == null)
			return null;

		try {
			user.setId(UUID.fromString(dto.getUserID()));
			user.setUsername(dto.getUsername());
			user.setSalt(dto.getSalt());
			user.setPassword(dto.getPassword());
			user.setAdmin(dto.isAdmin());
			user.setSessionID(UUID.fromString(dto.getSessionID()));
		}
		catch (IllegalArgumentException iaex) {
			return null;
		}

		return user;
	}
}
