package de.fmk.kicknrush.models.pojo;

import de.fmk.kicknrush.models.dto.GroupDTO;
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
public class Group {
	private int           groupID;
	private int           groupOrderID;
	private int           year;
	private LocalDateTime firstKickOff;
	private LocalDateTime lastKickOff;
	private String        groupName;


	/**
	 * Convert the data transfer object to an group object.
	 * @param dto The data transfer object.
	 * @return a group object or <code>null</code>.
	 */
	public static Group fromDTO(final GroupDTO dto) {
		final Group group;

		group = new Group();

		if (dto == null)
			return null;

		group.setGroupID(dto.getGroupID());
		group.setGroupOrderID(dto.getGroupOrderID());
		group.setYear(dto.getYear());
		group.setGroupName(dto.getGroupName());

		return group;
	}
}
