package de.fmk.kicknrush.app;

import com.airhacks.afterburner.injection.Injector;
import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.ResourceHelper;
import de.fmk.kicknrush.helper.ThreadHelper;
import de.fmk.kicknrush.views.login.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class App extends Application {
	private static Stage stage;

	private DatabaseHandler dbHandler;
	private ThreadHelper    threadHelper;


	@Override
	public void start(Stage primaryStage) throws Exception {
		final LoginView           login;
		final Map<Object, Object> customProperties;

		customProperties = new HashMap<>();
		customProperties.put("dbHandler", dbHandler);
		customProperties.put("threadHelper", threadHelper);

		Injector.setConfigurationSource(customProperties::get);

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
		final Properties properties;

		super.init();

		threadHelper = new ThreadHelper();
		properties   = ResourceHelper.loadProperties(DatabaseHandler.class, "db.properties");
		dbHandler    = new DatabaseHandler(properties);
		dbHandler.createInitialTables();
	}


	@Override
	public void stop() throws Exception
	{
		Injector.forgetAll();
		threadHelper.stopAllThreads();
		dbHandler.closeConnections();

		super.stop();
	}


	public static void main(String[] args) {
		launch(args);
	}


	public static synchronized Stage getPrimaryStage() {
		return stage;
	}


	private static synchronized void setPrimaryStage(Stage primaryStage) {
		stage = primaryStage;
	}
}
