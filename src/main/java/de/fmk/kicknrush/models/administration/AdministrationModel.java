package de.fmk.kicknrush.models.administration;

import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.UserCacheKey;
import de.fmk.kicknrush.models.AbstractStatusModel;
import de.fmk.kicknrush.models.pojo.User;
import de.fmk.kicknrush.service.RestService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Model of the {@link de.fmk.kicknrush.views.administration.AdministrationPresenter}.
 *
 * @author FabianK
 */
public class AdministrationModel extends AbstractStatusModel {
	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider cacheProvider;
	@Inject
	private RestService   restService;

	private ObservableList<User> users;


	/**
	 * Default constructor.
	 */
	public AdministrationModel() {
		super();
		init();
	}


	/**
	 * Create a new User and add it to the list.
	 * @param name Name of the user.
	 * @return the new user.
	 */
	public User createUser(final String name) {
		final User user = new User();

		user.setId(UUID.randomUUID());
		user.setUsername(name);

		users.add(user);

		return user;
	}


	/**
	 * Finally delete the user from the server.
	 * @param user The user to delete.
	 * @return <code>true</code>, if the user could have been deleted.
	 */
	public boolean delete(final User user) {
		if (user == null)
			return false;

		if (restService.deleteUser(user)) {
			removeUser(user);
			return true;
		}

		return false;
	}


	/**
	 * Initialize the user list.
	 */
	@PostConstruct
	public void init() {
		users = FXCollections.observableArrayList();
	}


	/**
	 * Load the existing users from the server and remove the currently interacting user from the list.
	 */
	public void load() {
		final List<User> userList;
		final String     userID;

		userID   = cacheProvider.getUserCache().getStringValue(UserCacheKey.USER_ID);
		userList = new ArrayList<>();

		restService.getUsers().forEach(user -> {
			if (!userID.equals(user.getId()))
				userList.add(user);
		});

		users.clear();
		users.addAll(userList);
	}


	/**
	 * Remove the given user from the list.
	 * @param user User to remove.
	 */
	public void removeUser(final User user) {
		users.remove(user);
	}


	/**
	 * Save the given user on the server.
	 * @param user User to save.
	 * @param createNew <code>true</code>, if the user is a new one.
	 * @return <code>true</code>, if the user could have been saved on the server.
	 */
	public boolean save(final User user, final boolean createNew) {
		return restService.administrateUser(user, createNew);
	}


	/**
	 * Get the list of users.
	 * @return the list of users.
	 */
	public ObservableList<User> getUsers() {
		return users;
	}
}
