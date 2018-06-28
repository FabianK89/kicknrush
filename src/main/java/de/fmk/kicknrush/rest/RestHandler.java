package de.fmk.kicknrush.rest;

import de.fmk.kicknrush.helper.CacheProvider;
import de.fmk.kicknrush.helper.UserCacheKey;
import de.fmk.kicknrush.models.pojo.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RestHandler {
	@Inject
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
		catch (Exception ex) {
			return null;
		}

		return null;
	}


	public boolean updateUser(final String username, final String password, final String salt) {
		final MultiValueMap<String, Object> values;
		final ResponseEntity<Boolean>       response;

		values = new LinkedMultiValueMap<>();
		values.add(UserCacheKey.SESSION.getKey(), cacheProvider.getUserValue(UserCacheKey.SESSION));
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

		if (HttpStatus.OK == response.getStatusCode())
		{
			for (final Object object : response.getBody())
			{
				if (object instanceof String)
					usernames.add((String) object);
			}
		}

		return usernames;
	}
}
