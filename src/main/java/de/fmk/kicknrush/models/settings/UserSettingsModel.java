package de.fmk.kicknrush.models.settings;

import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.UserCacheKey;
import de.fmk.kicknrush.rest.RestHandler;
import de.fmk.kicknrush.security.PasswordUtils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;


public class UserSettingsModel {
	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider cacheProvider;
	@Inject
	private RestHandler   restHandler;

	private List<String> usernames;


	@PostConstruct
	public void init() {
		usernames = restHandler.getUsernames();
		usernames.remove(cacheProvider.getUserCache().getStringValue(UserCacheKey.USERNAME));
	}


	public boolean isOldPasswordCorrect(final String password) {
		if (password == null || password.isEmpty())
			return false;

		if (cacheProvider.getUserCache().getBooleanValue(UserCacheKey.CHANGE_PWD, false)) {
			return password.equals(cacheProvider.getUserCache().getStringValue(UserCacheKey.PASSWORD));
		}
		else {
			return PasswordUtils.verifyUserPassword(password,
			                                        cacheProvider.getUserCache().getStringValue(UserCacheKey.PASSWORD),
			                                        cacheProvider.getUserCache().getStringValue(UserCacheKey.SALT));
		}
	}


	public boolean isUniqueName(final String username) {
		if (username == null || username.isEmpty())
			return false;

		return !usernames.contains(username);
	}


	public boolean passwordsAreEqual(final String password1, final String password2) {
		if (password1 == null || password1.isEmpty() || password2 == null || password2.isEmpty())
			return false;

		return password1.equals(password2);
	}


	public void save(final String username, final String newPassword) {
		final String salt;
		final String secretPassword;

		salt           = PasswordUtils.getSalt(255);
		secretPassword = PasswordUtils.generateSecurePassword(newPassword, salt);

		cacheProvider.getUserCache().putStringValue(UserCacheKey.USERNAME, username);
		cacheProvider.getUserCache().putStringValue(UserCacheKey.PASSWORD, secretPassword);
		cacheProvider.getUserCache().putStringValue(UserCacheKey.SALT, salt);

		restHandler.updateUser(username, secretPassword, salt);
	}
}
