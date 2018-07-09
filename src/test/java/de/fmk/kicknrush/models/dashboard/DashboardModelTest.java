package de.fmk.kicknrush.models.dashboard;

import de.fmk.kicknrush.helper.cache.CacheProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


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

//		assertFalse(model.hasDefaultPassword());
//		assertTrue(model.hasDefaultPassword());
	}
}