package de.fmk.kicknrush.models.bets.matches;

import de.fmk.kicknrush.models.pojo.Group;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @author FabianK
 */
@RunWith(JUnitPlatform.class)
class MatchesModelTest {
	private MatchesModel model;


	@BeforeEach
	void setUp() {
		model = new MatchesModel();
	}


	@AfterEach
	void tearDown() {
		model = null;
	}


	@Test
	void testGetActualGroup() {
		final Group         group;
		final List<Group>   groupList;
		final LocalDateTime now;

		now = LocalDateTime.now();

		groupList = new ArrayList<>();
		groupList.add(mockGroup(1, now.minus(5, ChronoUnit.DAYS), now.minus(4, ChronoUnit.DAYS)));
		groupList.add(mockGroup(2, now.minus(3, ChronoUnit.DAYS), now.minus(2, ChronoUnit.DAYS)));
		groupList.add(mockGroup(3, now.plus(1, ChronoUnit.DAYS), now.plus(2, ChronoUnit.DAYS)));
		groupList.add(mockGroup(4, now.plus(3, ChronoUnit.DAYS), now.plus(4, ChronoUnit.DAYS)));

		group = model.getActualGroup(groupList);

		assertEquals(3, group.getGroupID());
	}


	private Group mockGroup(int id, LocalDateTime first, LocalDateTime last) {
		final Group group;

		group = mock(Group.class);
		when(group.getGroupID()).thenReturn(id);
		when(group.getFirstKickOff()).thenReturn(first);
		when(group.getLastKickOff()).thenReturn(last);

		return group;
	}
}