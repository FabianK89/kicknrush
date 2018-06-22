package de.fmk.kicknrush.models.dashboard;

import de.fmk.kicknrush.helper.CacheProvider;
import de.fmk.kicknrush.helper.UserCacheKey;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;


public class DashboardModelTest {
	@Mock
	private CacheProvider  m_cacheProvider;
	@InjectMocks
	private DashboardModel model;


	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}


	@Test
	public void testHasDefaultPassword() {
		when(m_cacheProvider.getUserValue(UserCacheKey.PASSWORD)).thenReturn("1234", "admin123");

		assertFalse(model.hasDefaultPassword());
		assertTrue(model.hasDefaultPassword());
	}
}