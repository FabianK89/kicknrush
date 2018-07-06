package de.fmk.kicknrush;

import java.util.function.Consumer;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;


public abstract class TestBase {
	public <T> void throwsIllegalArgumentException(T object, Consumer<T> method) {
		try {
			method.accept(object);
			fail("An IllegalArgumentException must occur.");
		}
		catch (Exception ex) {
			assertTrue(ex instanceof IllegalArgumentException);
		}
	}
}
