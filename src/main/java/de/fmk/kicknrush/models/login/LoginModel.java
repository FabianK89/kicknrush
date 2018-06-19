package de.fmk.kicknrush.models.login;

import de.fmk.kicknrush.models.Status;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import javax.annotation.PostConstruct;


public class LoginModel {
	private ObjectProperty<Status> status;


	@PostConstruct
	public void init() {
		status = new SimpleObjectProperty<>(Status.INITIAL);
	}


	public ObjectProperty<Status> statusProperty()
	{
		return status;
	}


	public void login(final String username, final String password) {
		final Thread loginThread;

		status.set(Status.RUNNING);

		loginThread = new Thread(() -> {
			try {
				Thread.sleep(3000);
			}
			catch (InterruptedException p_e) {
				p_e.printStackTrace();
			}

			if ("Admin".equals(username) && "admin123".equals(password))
				Platform.runLater(() -> status.set(Status.SUCCESS));
			else
				Platform.runLater(() -> status.set(Status.FAILED));
		});

		loginThread.start();
	}
}
