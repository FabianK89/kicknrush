package de.fmk.kicknrush.models.administration.teamadministration;

import de.fmk.kicknrush.helper.ThreadHelper;
import de.fmk.kicknrush.helper.UpdateHelper;
import de.fmk.kicknrush.models.AbstractStatusModel;
import de.fmk.kicknrush.models.Status;

import javax.annotation.PostConstruct;
import javax.inject.Inject;


/**
 * @author FabianK
 */
public class TeamAdministrationModel extends AbstractStatusModel {
	@Inject
	private UpdateHelper updateHelper;
	@Inject
	private ThreadHelper threadHelper;


	@PostConstruct
	public void init() {

	}


	public void loadTeams() {
		final Thread thread;

		statusProperty.set(Status.RUNNING);

		thread = new Thread(() -> {
			updateHelper.checkForUpdates();
			statusProperty().set(Status.SUCCESS);
		});

		threadHelper.addThread(thread);

		thread.start();
	}
}
