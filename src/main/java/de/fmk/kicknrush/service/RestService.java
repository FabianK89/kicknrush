package de.fmk.kicknrush.service;

import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.UserCacheKey;
import de.fmk.kicknrush.models.pojo.Team;
import de.fmk.kicknrush.models.pojo.Update;
import de.fmk.kicknrush.models.pojo.User;
import javafx.collections.FXCollections;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RestService
{
	@Inject @Named(CacheProvider.CACHE_ID)
	private CacheProvider cacheProvider;

	private String       baseUrl;
	private RestTemplate restTemplate;


	@PostConstruct
	public void init() {
		baseUrl      = "http://localhost:8080/api";
		restTemplate = new RestTemplate();
	}


	public User loginUser(final String username, final String password) {
		final Map<String, String>  uriVariables;
		final ResponseEntity<User> response;

		uriVariables = new HashMap<>();
		uriVariables.put("username", username);
		uriVariables.put("password", password);

		try {
			response = restTemplate.getForEntity(baseUrl.concat("/user/login?username={username}&password={password}"),
			                                     User.class,
			                                     uriVariables);

			if (HttpStatus.OK == response.getStatusCode())
				return response.getBody();
		}
		catch (HttpClientErrorException hceex) {
			return null;
		}

		return null;
	}


	public boolean logout() {
		final Map<String, String>    uriVariables;
		final ResponseEntity<String> response;

		uriVariables = new HashMap<>();
		uriVariables.put("userID", cacheProvider.getUserCache().getStringValue(UserCacheKey.USER_ID));

		try {
			response = restTemplate.getForEntity(baseUrl.concat("/user/logout?userID={userID}"), String.class, uriVariables);

			return HttpStatus.OK == response.getStatusCode();
		}
		catch (HttpServerErrorException hseex) {
			return false;
		}
	}


	public boolean administrateUser(final User user, final boolean isNew) {
		final MultiValueMap<String, Object> values;
		final ResponseEntity<Boolean>       response;

		values = new LinkedMultiValueMap<>();
		values.add(UserCacheKey.SESSION.getKey(), cacheProvider.getUserCache().getStringValue(UserCacheKey.SESSION));
		values.add(UserCacheKey.USER_ID.getKey(), user.getId());
		values.add(UserCacheKey.USERNAME.getKey(), user.getUsername());
		values.add(UserCacheKey.IS_ADMIN.getKey(), Boolean.toString(user.isAdmin()));
		values.add("create.new", Boolean.toString(isNew));

		response = restTemplate.postForEntity(baseUrl.concat("/user/admin/administrateUser"), values, Boolean.class);

		if (HttpStatus.OK == response.getStatusCode())
			return response.getBody();

		return false;
	}


	public boolean deleteUser(final User user) {
		final MultiValueMap<String, Object> values;
		final ResponseEntity<Boolean>       response;

		values = new LinkedMultiValueMap<>();
		values.add(UserCacheKey.SESSION.getKey(), cacheProvider.getUserCache().getStringValue(UserCacheKey.SESSION));
		values.add(UserCacheKey.USER_ID.getKey(), user.getId());

		response = restTemplate.postForEntity(baseUrl.concat("/user/admin/deleteUser"), values, Boolean.class);

		if (HttpStatus.OK == response.getStatusCode())
			return response.getBody();

		return false;
	}


	public boolean updateUser(final String username, final String password, final String salt) {
		final MultiValueMap<String, Object> values;
		final ResponseEntity<Boolean>       response;

		values = new LinkedMultiValueMap<>();
		values.add(UserCacheKey.SESSION.getKey(), cacheProvider.getUserCache().getStringValue(UserCacheKey.SESSION));
		values.add(UserCacheKey.USERNAME.getKey(), username);
		values.add(UserCacheKey.PASSWORD.getKey(), password);
		values.add(UserCacheKey.SALT.getKey(), salt);

		response = restTemplate.postForEntity(baseUrl.concat("/user/updateUser"), values, Boolean.class);

		if (HttpStatus.OK == response.getStatusCode())
			return response.getBody();

		return false;
	}


	public List<String> getUsernames() {
		final List<String>         usernames;
		final ResponseEntity<List> response;

		usernames = new ArrayList<>();
		response  = restTemplate.getForEntity(baseUrl.concat("/user/getUsernames"), List.class);

		if (HttpStatus.OK == response.getStatusCode()) {
			for (final Object object : response.getBody()) {
				if (object instanceof String)
					usernames.add((String) object);
			}
		}

		return usernames;
	}


	public List<Team> getTeams() {
		final Team[] teams;

		teams = restTemplate.getForObject(baseUrl.concat("/league/getTeams"), Team[].class);

		if (teams == null)
			return Collections.emptyList();

		return Arrays.asList(teams);
	}


	public List<Update> getUpdates() {
		final Update[] updates;

		updates = restTemplate.getForObject(baseUrl.concat("/updates/getAll"), Update[].class);

		if (updates == null)
			return Collections.emptyList();

		return Arrays.asList(updates);
	}


	public List<User> getUsers() {
		final ResponseEntity<User[]> response;

		response = restTemplate.getForEntity(baseUrl.concat("/user/getUsers"), User[].class);

		if (HttpStatus.OK == response.getStatusCode())
			return FXCollections.observableArrayList(response.getBody());

		return Collections.emptyList();
	}
}
