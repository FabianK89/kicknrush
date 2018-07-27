package de.fmk.kicknrush.views;

/**
 * @author FabianK
 */
public enum AppImage {
	CONNECTION_PROBLEM("notification_connection_problem.png"),
	LOADING("loading.gif"),
	LOGIN_FAILED("notification_login_failed.png"),
	SUCCESS("notification_success.png");


	private String name;


	AppImage(String name) {
		this.name = name;
	}


	/**
	 * @return the name of the image.
	 */
	public String getName() {
		return name;
	}
}
