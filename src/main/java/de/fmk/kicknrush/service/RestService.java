package de.fmk.kicknrush.service;

import de.fmk.kicknrush.helper.cache.CacheProvider;
import de.fmk.kicknrush.helper.cache.UserCacheKey;
import de.fmk.kicknrush.models.dto.UserDTO;
import de.fmk.kicknrush.models.pojo.Team;
import de.fmk.kicknrush.models.pojo.Update;
import de.fmk.kicknrush.models.pojo.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
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
		final ResponseEntity<UserDTO> response;
		final UserDTO                 body;

		body = new UserDTO();
		body.setUsername(username);
		body.setPassword(password);

		try {
			response = restTemplate.postForEntity(baseUrl.concat("/user/login"), body, UserDTO.class);

			if (HttpStatus.OK == response.getStatusCode())
				return User.fromDTO(response.getBody());
		}
		catch (HttpClientErrorException hceex) {
			return null;
		}

		return null;
	}


	public boolean logout() {
		final ResponseEntity<String> response;
		final UserDTO                body;

		body = new UserDTO();
		body.setSessionID(cacheProvider.getUserCache().getStringValue(UserCacheKey.SESSION));
		body.setUserID(cacheProvider.getUserCache().getStringValue(UserCacheKey.USER_ID));

		try {
			response = restTemplate.postForEntity(baseUrl.concat("/user/logout"), body, String.class);

			return HttpStatus.OK == response.getStatusCode();
		}
		catch (HttpClientErrorException hceex) {
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
		final ResponseEntity<String> response;
		final UserDTO                 body;

		body = user.toDTO();
		body.setSessionID(cacheProvider.getUserCache().getStringValue(UserCacheKey.SESSION));

		response = restTemplate.postForEntity(baseUrl.concat("/user/admin/deleteUser"), body, String.class);

		return HttpStatus.OK == response.getStatusCode();
	}


	public boolean updateUser(final String username, final String password, final String salt) {
		final ResponseEntity<Boolean> response;
		final UserDTO                 body;

		body = new UserDTO();
		body.setSessionID(cacheProvider.getUserCache().getStringValue(UserCacheKey.SESSION));
		body.setUserID(cacheProvider.getUserCache().getStringValue(UserCacheKey.USER_ID));
		body.setUsername(username);
		body.setPassword(password);
		body.setSalt(salt);

		response = restTemplate.postForEntity(baseUrl.concat("/user/updateUser"), body, Boolean.class);

		if (HttpStatus.OK == response.getStatusCode())
			return response.getBody();

		return false;
	}


	public List<String> getUsernames() {
		final Map<String, String>      uriVariables;
		final List<String>             usernames;
		final ResponseEntity<String[]> response;

		uriVariables = new HashMap<>();
		uriVariables.put("sessionID", cacheProvider.getUserCache().getStringValue(UserCacheKey.SESSION));
		uriVariables.put("userID", cacheProvider.getUserCache().getStringValue(UserCacheKey.USER_ID));

		usernames = new ArrayList<>();
		response  = restTemplate.getForEntity(baseUrl.concat("/user/getUsernames?sessionID={sessionID}&userID={userID}"),
		                                      String[].class,
		                                      uriVariables);

		if (HttpStatus.OK == response.getStatusCode()) {
			if (response.getBody() == null || response.getBody().length == 0)
				return usernames;

			return Arrays.asList(response.getBody());
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
		final List<User>                result;
		final Map<String, String>       uriVariables;
		final ResponseEntity<UserDTO[]> response;

		uriVariables = new HashMap<>();
		uriVariables.put("sessionID", cacheProvider.getUserCache().getStringValue(UserCacheKey.SESSION));
		uriVariables.put("userID", cacheProvider.getUserCache().getStringValue(UserCacheKey.USER_ID));

		response = restTemplate.getForEntity(baseUrl.concat("/user/getUsers"), UserDTO[].class, uriVariables);
		result   = new ArrayList<>();

		if (HttpStatus.OK == response.getStatusCode() && response.getBody() != null) {
			for (final UserDTO dto : response.getBody())
				result.add(User.fromDTO(dto));
		}

		return result;
	}
}
