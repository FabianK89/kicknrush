package de.fmk.kicknrush.models.administration;

import de.fmk.kicknrush.models.AbstractStatusModel;
import de.fmk.kicknrush.models.pojo.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.annotation.PostConstruct;
import java.util.UUID;


/**
 * @author FabianK
 */
public class AdministrationModel extends AbstractStatusModel {
	private ObservableList<User> users;
	private User                 editedUser;


	public AdministrationModel() {
		super();
		init();
	}


	@PostConstruct
	public void init() {
		users = FXCollections.observableArrayList();
	}


	public void createUser() {
		final User user = new User();

		user.setId(UUID.randomUUID().toString());
		user.setUsername("Neuer Benutzer");

		editedUser = user;
	}


	public ObservableList<User> getUsers() {
		return users;
	}
}
