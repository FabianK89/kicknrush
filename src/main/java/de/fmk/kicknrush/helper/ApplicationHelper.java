package de.fmk.kicknrush.helper;


import de.fmk.kicknrush.app.App;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


/**
 * Helper class for the application.
 */
public class ApplicationHelper {
	/**
	 * Change the view of the application to the given root pane.
	 * @param root The root pane of the new view.
	 * @param resizable <code>true</code>, if the stage should be resizable, otherwise <code>false</code>.
	 */
	public void changeView(final Parent root, final boolean resizable) {
		final Pane  pane;
		final Stage primaryStage;

		if (!(root instanceof Pane))
			throw new IllegalArgumentException("The parameter 'root' must be an instance of Pane.");

		primaryStage = App.getPrimaryStage();
		pane         = (Pane) root;

		primaryStage.setScene(new Scene(root, pane.getPrefWidth(), pane.getPrefHeight()));
		primaryStage.setResizable(resizable);
		primaryStage.centerOnScreen();
	}
}
