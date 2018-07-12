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


	public User createUser(final String name) {
		final User user = new User();

		user.setId(UUID.randomUUID().toString());
		user.setUsername(name);

		editedUser = user;
		users.add(user);

		return editedUser;
	}


	public User getEditedUser() {
		return editedUser;
	}


	public void removeUser(final User user) {
		editedUser = null;
		users.remove(user);
	}


	public ObservableList<User> getUsers() {
		return users;
	}
}
