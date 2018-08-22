package de.fmk.kicknrush.models.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import de.fmk.kicknrush.models.dto.TeamDTO;
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
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.UpperCamelCaseStrategy.class)
public class Team {
	private int    teamId;
	private String teamIconUrl;
	private String teamIconUrlSmall;
	private String teamName;


	/**
	 * Convert the data transfer object to an team object.
	 * @param dto The data transfer object.
	 * @return a team object or <code>null</code>.
	 */
	public static Team fromDTO(final TeamDTO dto) {
		final Team team;

		team = new Team();

		if (dto == null)
			return null;

		team.setTeamId(dto.getTeamId());
		team.setTeamName(dto.getTeamName());

		return team;
	}
}
