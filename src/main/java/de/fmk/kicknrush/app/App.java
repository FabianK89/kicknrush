package de.fmk.kicknrush.app;

import com.airhacks.afterburner.injection.Injector;
import de.fmk.kicknrush.db.DatabaseHandler;
import de.fmk.kicknrush.helper.ResourceHelper;
import de.fmk.kicknrush.views.Notification;
import de.fmk.kicknrush.views.login.LoginPresenter;
import de.fmk.kicknrush.views.login.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.controlsfx.control.NotificationPane;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class App extends Application {
	private static Stage stage;

	private DatabaseHandler dbHandler;


	@Override
	public void start(Stage primaryStage) throws Exception {
		final LoginView           loginView;
		final Map<Object, Object> customProperties;
		final Notification        notificationPane;
		final String              notificationCss;

		customProperties = new HashMap<>();
		customProperties.put("dbHandler", dbHandler);

		Injector.setConfigurationSource(customProperties::get);

		setPrimaryStage(primaryStage);

		notificationCss = this.getClass().getResource("notificationpane.css").toExternalForm();

		loginView        = new LoginView();
		notificationPane = new Notification(loginView.getView());

		((LoginPresenter) loginView.getPresenter()).setNotificationPane(notificationPane);

		primaryStage.setTitle("Kick'n'Rush");
		primaryStage.getIcons().addAll(ResourceHelper.getAppIcons());
		primaryStage.setScene(new Scene(notificationPane));
		primaryStage.getScene().getStylesheets().add(notificationCss);
		primaryStage.setResizable(false);
		primaryStage.show();
	}


	@Override
	public void init() throws Exception {
		final Properties properties;

		super.init();

		properties = ResourceHelper.loadProperties(DatabaseHandler.class, "db.properties");
		dbHandler  = new DatabaseHandler(properties);
		dbHandler.createInitialTables();
	}


	@Override
	public void stop() throws Exception
	{
		Injector.forgetAll();
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
