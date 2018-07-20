package de.fmk.kicknrush.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


/**
 * @author FabianK
 */
public class ThreadHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadHelper.class);

	private final Map<String, Thread> threads;


	public ThreadHelper() {
		threads = new HashMap<>();
	}


	@PostConstruct
	public void init() {
		LOGGER.info("ThreadHelper has been initialized.");
	}


	public void addThread(Thread thread) {
		final Thread existingThread;

		existingThread = threads.get(thread.getName());

		if (existingThread == null || !existingThread.isAlive())
			threads.put(thread.getName(), thread);
	}


	public void stopAllThreads() {
		LOGGER.info("Interrupt all existing threads.");

		threads.forEach((name, thread) -> {
			if (thread != null && thread.isAlive()) {
				LOGGER.info("Interrupt thread with name '{}'.", name);
				thread.interrupt();
			}
		});
	}
}
