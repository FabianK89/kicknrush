package de.fmk.kicknrush.helper;


import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public final class ApplicationHelper {
	private static ApplicationHelper instance;

	private Stage primaryStage;


	private ApplicationHelper() {}


	public static ApplicationHelper getInstance() {
		if (instance == null)
			instance = new ApplicationHelper();

		return instance;
	}


	public void changeView(final Parent root, final boolean resizable) {
		final Pane pane;

		if (!(root instanceof Pane))
			throw new IllegalArgumentException("The parameter 'root' must be an instance of Pane.");

		pane = (Pane) root;

		primaryStage.setScene(new Scene(root, pane.getPrefWidth(), pane.getPrefHeight()));
		primaryStage.setResizable(resizable);
		primaryStage.centerOnScreen();
	}


	public Stage getPrimaryStage() {
		return primaryStage;
	}


	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
}
