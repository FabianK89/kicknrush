package de.fmk.kicknrush.models.administration;

import de.fmk.kicknrush.models.pojo.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author FabianK
 */
@RunWith(MockitoJUnitRunner.class)
class AdministrationModelTest {
	@InjectMocks
	private AdministrationModel model;


	@BeforeAll
	void setUp() {
		MockitoAnnotations.initMocks(this);
	}


	@Test
	void testGetUsers() {
		final User user;

		assertTrue(model.getUsers().isEmpty());

		user = model.createUser("Test");

		assertEquals(1, model.getUsers().size());
		assertEquals("Test", model.getUsers().get(0).getUsername());
		assertEquals(user, model.getEditedUser());
	}
}