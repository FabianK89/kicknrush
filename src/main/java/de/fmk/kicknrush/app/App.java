package de.fmk.kicknrush.app;

import de.fmk.kicknrush.helper.ApplicationHelper;
import de.fmk.kicknrush.views.login.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class App extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		final LoginView login;

		login = new LoginView();

		ApplicationHelper.getInstance().setPrimaryStage(primaryStage);

		primaryStage.setTitle("Kick'n'Rush");
		primaryStage.setScene(new Scene(login.getView()));
		primaryStage.setResizable(false);
		primaryStage.show();
	}


	@Override
	public void init() throws Exception {
		super.init();
	}


	public static void main(String[] args) {
		launch(args);
	}
}
