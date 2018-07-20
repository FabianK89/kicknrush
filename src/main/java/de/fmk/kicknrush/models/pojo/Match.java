package de.fmk.kicknrush.models.pojo;

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
	private LocalDateTime kickOff;
	private String        id;
	private String        guestTeam;
	private String        homeTeam;
}
