package de.fmk.kicknrush.models.pojo;

import de.fmk.kicknrush.helper.TimeUtils;
import de.fmk.kicknrush.models.dto.MatchDTO;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


/**
 * @author FabianK
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Match {
	private boolean       finished;
	private int           id;
	private Group         group;
	private LocalDateTime kickOff;
	private Team          guestTeam;
	private Team          homeTeam;


	/**
	 * Convert the data transfer object to an match object.
	 * @param dto The data transfer object.
	 * @return a match object or <code>null</code>.
	 */
	public static Match fromDTO(final MatchDTO dto) {
		final Group group;
		final Match match;
		final Team  guest;
		final Team  home;

		match = new Match();

		if (dto == null)
			return null;

		group = new Group();
		group.setGroupID(dto.getGroupID());

		home = new Team();
		home.setTeamId(dto.getTeamHomeID());

		guest = new Team();
		guest.setTeamId(dto.getTeamGuestID());

		match.setId(dto.getMatchID());
		match.setKickOff(TimeUtils.createLocalDateTime(dto.getMatchDateTimeUTC()));
		match.setFinished(dto.isMatchIsFinished());
		match.setGroup(group);
		match.setHomeTeam(home);
		match.setGuestTeam(guest);

		return match;
	}
}
