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

	public static final String UPDATE_THREAD = "updateThread";

	private final Map<String, Thread> threads;


	public ThreadHelper() {
		threads = new HashMap<>();
	}


	@PostConstruct
	public void init() {
		LOGGER.info("ThreadHelper has been initialized.");
	}


	public void addThread(final Thread thread) {
		final Thread existingThread;

		existingThread = threads.get(thread.getName());

		if (existingThread == null || !existingThread.isAlive())
			threads.put(thread.getName(), thread);
	}


	public boolean isThreadRunning(final String threadName) {
		if (threadName == null || !threads.keySet().contains(threadName))
			return false;

		return threads.get(threadName).isAlive() && !threads.get(threadName).isInterrupted();
	}


	public void removeThread(final String threadName) {
		if (threads.remove(threadName) != null)
			LOGGER.info("Thread '{}' has finished his job.", threadName);
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
