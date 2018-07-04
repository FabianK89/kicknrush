package de.fmk.kicknrush.app;

import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.ResourceHelper;
import de.fmk.kicknrush.views.login.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Properties;


public class App extends Application {
	private static Stage stage;


	@Override
	public void start(Stage primaryStage) throws Exception {
		final LoginView login;

		setPrimaryStage(primaryStage);

		login = new LoginView();

		primaryStage.setTitle("Kick'n'Rush");
		primaryStage.getIcons().addAll(ResourceHelper.getAppIcons());
		primaryStage.setScene(new Scene(login.getView()));
		primaryStage.setResizable(false);
		primaryStage.show();
	}


	@Override
	public void init() throws Exception {
		final DatabaseHandler dbHandler;
		final Properties      properties;

		super.init();

		properties = ResourceHelper.loadProperties(DatabaseHandler.class, "db.properties");
		dbHandler  = new DatabaseHandler(properties);
		dbHandler.createInitialTables();
	}


	@Override
	public void stop() throws Exception
	{
		super.stop();
	}


	public static void main(String[] args) {
		launch(args);
	}


	public static Stage getPrimaryStage() {
		return stage;
	}


	public synchronized static void setPrimaryStage(Stage primaryStage) {
		stage = primaryStage;
	}
}
