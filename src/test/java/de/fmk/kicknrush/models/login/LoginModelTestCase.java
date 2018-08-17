package de.fmk.kicknrush.models.login;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.UserCache;
import de.fmk.kicknrush.helper.cache.UserCacheKey;
import de.fmk.kicknrush.models.Status;
import de.fmk.kicknrush.models.pojo.User;
import de.fmk.kicknrush.service.RestService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.JFXPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LoginModelTestCase {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginModelTestCase.class);

	private static final String ADMIN = "Admin";
	private static final String PWD   = "pwd";

	@Mock
	private CacheProvider   cacheProvider;
	@Mock
	private DatabaseHandler dbHandler;
	@Mock
	private RestService     restService;
	@Mock
	private UserCache       userCache;

	@InjectMocks
	private LoginModel loginModel;


	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void testInit() {
		final LoginModel model = new LoginModel();

		model.init();

		assertEquals(Status.INITIAL, model.statusProperty().get());
	}


	@Test
	public void testStatusProperty() {
		final LoginModel model = new LoginModel();

		assertNull(model.statusProperty());

		model.init();
		model.statusProperty().set(Status.SUCCESS);

		assertEquals(Status.SUCCESS, model.statusProperty().get());
	}


	@Test
	public void testLogin() throws Exception {
		final ArgumentCaptor<String> valueCapture;
		final ObjectProperty<Status> statusProperty;
		final User                   user;

		user = new User(true, PWD, "salt", ADMIN, UUID.fromString("sessionid"), UUID.fromString("uuid"));
		valueCapture = ArgumentCaptor.forClass(String.class);

		initializeJavaFX();

		loginModel.init();

		statusProperty = new SimpleObjectProperty<>();
		statusProperty.bind(loginModel.statusProperty());

		// database handler
		assertNotNull(dbHandler);

		doNothing().when(dbHandler).updateUser(user);

		// cache
		assertNotNull(cacheProvider);

		when(cacheProvider.getUserCache()).thenReturn(userCache);

		assertNotNull(userCache);

		doNothing().when(userCache).removeValue(isA(UserCacheKey.class));
		doNothing().when(userCache).putStringValue(eq(UserCacheKey.PASSWORD), valueCapture.capture());

		// rest handler
		assertNotNull(restService);

		when(restService.loginUser(null, null)).thenReturn(null);
		when(restService.loginUser(ADMIN, PWD)).thenReturn(user);

		loginModel.login(null, null);

		await().atMost(2, TimeUnit.SECONDS).until(() -> Status.RUNNING != statusProperty.get());
		assertEquals(Status.FAILED, loginModel.statusProperty().get());

		loginModel.login("Test", "abc");

		await().atMost(2, TimeUnit.SECONDS).until(() -> Status.RUNNING != statusProperty.get());
		assertEquals(Status.FAILED, loginModel.statusProperty().get());

		loginModel.login(ADMIN, PWD);

		await().atMost(2, TimeUnit.SECONDS).until(() -> Status.RUNNING != statusProperty.get());
		verify(dbHandler).updateUser(user);
		assertEquals(Status.SUCCESS, loginModel.statusProperty().get());
		assertEquals(PWD, valueCapture.getValue());
	}


	private void initializeJavaFX() {
		final CountDownLatch latch;

		latch = new CountDownLatch(1);

		SwingUtilities.invokeLater(() -> {
			new JFXPanel(); // initializes JavaFX environment

			latch.countDown();
		});

		try {
			latch.await(3, TimeUnit.SECONDS);
		}
		catch (InterruptedException iex) {
			LOGGER.warn("Thread has been interrupted in initializeJavaFX.", iex);
		}
	}
}
