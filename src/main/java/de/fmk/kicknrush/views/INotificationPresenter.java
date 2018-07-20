package de.fmk.kicknrush.views;

import org.controlsfx.control.NotificationPane;


/**
 * Interface for presenter that controls a notification pane.
 *
 * @author FabianK
 */
public interface INotificationPresenter extends IPaneHandler {
	/**
	 * Set the notification pane.
	 * @param pane The notification pane.
	 */
	void setNotificationPane(Notification pane);
}
