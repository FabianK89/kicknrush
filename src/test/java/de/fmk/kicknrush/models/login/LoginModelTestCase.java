package de.fmk.kicknrush.models.login;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.UserCacheKey;
import de.fmk.kicknrush.models.Status;
import de.fmk.kicknrush.models.pojo.User;
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
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LoginModelTestCase {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoginModelTestCase.class);

	private static final String ADMIN = "Admin";
	private static final String PWD   = "pwd";

	@Mock
	private CacheProvider   m_cacheProvider;
	@Mock
	private DatabaseHandler dbHandler;

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

		valueCapture = ArgumentCaptor.forClass(String.class);

		initializeJavaFX();

		loginModel.init();

		statusProperty = new SimpleObjectProperty<>();
		statusProperty.bind(loginModel.statusProperty());

		assertNotNull(dbHandler);

		when(dbHandler.loginUser(null, null)).thenThrow(new SQLException());
		when(dbHandler.loginUser("Test", "abc")).thenReturn(null);
		when(dbHandler.loginUser(ADMIN, PWD)).thenReturn(new User("uuid", true, PWD, "salt", ADMIN, "sessionid"));

		doNothing().when(m_cacheProvider).putUserValue(isA(UserCacheKey.class), valueCapture.capture());

		loginModel.login(null, null);

		await().atMost(2, TimeUnit.SECONDS).until(() -> Status.RUNNING != statusProperty.get());
		assertEquals(Status.FAILED, loginModel.statusProperty().get());

		loginModel.login("Test", "abc");

		await().atMost(2, TimeUnit.SECONDS).until(() -> Status.RUNNING != statusProperty.get());
		assertEquals(Status.FAILED, loginModel.statusProperty().get());
		assertNull(m_cacheProvider.getStringUserValue(UserCacheKey.USERNAME));

		loginModel.login(ADMIN, PWD);

		await().atMost(2, TimeUnit.SECONDS).until(() -> Status.RUNNING != statusProperty.get());
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
