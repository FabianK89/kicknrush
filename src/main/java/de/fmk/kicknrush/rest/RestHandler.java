package de.fmk.kicknrush.rest;

import de.fmk.kicknrush.models.pojo.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;


public class RestHandler {
	private RestTemplate restTemplate;


	@PostConstruct
	public void init() {
		restTemplate = new RestTemplate();
	}


	public User loginUser(final String username, final String password) {
		final Map<String, String>  uriVariables;
		final ResponseEntity<User> response;


		uriVariables = new HashMap<>();
		uriVariables.put("username", username);
		uriVariables.put("password", password);

		try {
			response = restTemplate.getForEntity("http://localhost:8080/login?username={username}&password={password}",
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
}
