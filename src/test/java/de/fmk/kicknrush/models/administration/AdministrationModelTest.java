package de.fmk.kicknrush.models.administration;

import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.UserCache;
import de.fmk.kicknrush.helper.cache.UserCacheKey;
import de.fmk.kicknrush.models.pojo.User;
import de.fmk.kicknrush.rest.RestHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * @author FabianK
 */
@RunWith(MockitoJUnitRunner.class)
class AdministrationModelTest {
	@Mock
	private CacheProvider cacheProvider;
	@Mock
	private RestHandler   restHandler;
	@Mock
	private UserCache     userCache;

	@InjectMocks
	private AdministrationModel model;


	@BeforeEach
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

		model.removeUser(user);

		assertTrue(model.getUsers().isEmpty());
	}


	@Test
	void testLoad() {
		final List<User> list;

		list = new ArrayList<>();
		list.add(new User("A", true, null, null, "Ulf", null));
		list.add(new User("B", true, null, null, "Admin", null));
		list.add(new User("C", true, null, null, "Alf", null));

		assertNotNull(restHandler);
		when(restHandler.getUsers()).thenReturn(list);

		assertNotNull(userCache);
		when(userCache.getStringValue(UserCacheKey.USER_ID)).thenReturn("B");

		assertNotNull(cacheProvider);
		when(cacheProvider.getUserCache()).thenReturn(userCache);

		assertTrue(model.getUsers().isEmpty());

		model.load();

		assertEquals(2, model.getUsers().size());
		assertEquals("Ulf", model.getUsers().get(0).getUsername());
		assertEquals("Alf", model.getUsers().get(1).getUsername());
	}


	@Test
	void testDelete() {
		final User user = model.createUser("Test");

		assertNotNull(restHandler);
		when(restHandler.deleteUser(isA(User.class))).thenReturn(false, true);

		assertFalse(model.delete(null));
		assertFalse(model.delete(new User()));
		assertTrue(model.getUsers().contains(user));
		assertTrue(model.delete(user));
		assertFalse(model.getUsers().contains(user));
	}


	@Test
	void testSave() {
		assertNotNull(restHandler);
		when(restHandler.administrateUser(isA(User.class), isA(Boolean.class))).thenReturn(true);

		assertTrue(model.save(new User(), true));
		verify(restHandler).administrateUser(isA(User.class), isA(Boolean.class));
	}
}