package it.vige.labs.gc.users;

import static java.util.Arrays.asList;
import static org.jboss.logging.Logger.getLogger;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

@Component
public class Authorities implements Serializable, Converters {

	public final static String ADMIN_ROLE = "ROLE_ADMIN";

	public final static String CITIZEN_ROLE = "ROLE_CITIZEN";

	private static Logger log = getLogger(Authorities.class);

	@Autowired
	private RestTemplate restTemplate;

	@Value("${keycloak.auth-server-url}")
	private URI usersURI;

	@Value("${keycloak.realm}")
	private String usersRealm;

	public boolean hasRole(String... role) {
		List<String> roles = asList(role);
		Authentication authentication = getContext().getAuthentication();
		return authentication.getAuthorities().parallelStream().anyMatch(r -> roles.contains(r.getAuthority()));
	}

	public User getUser() throws ModuleException {
		Authentication authentication = getContext().getAuthentication();
		String id = authentication.getName();
		UserRepresentation user = null;
		try {
			UriComponents uriComponents = newInstance().uri(getFindUserByIdURI(id)).buildAndExpand();

			ResponseEntity<UserRepresentation> response = restTemplate.exchange(uriComponents.toString(), GET, null,
					UserRepresentation.class);
			user = response.getBody();
		} catch (Exception e) {
			String message = "Cannot find user by id " + id;
			throw new ModuleException(message, e);
		}
		log.debug("user found: " + user);
		User result = UserRepresentationToUser.apply(user);
		result.setRoles(authentication.getAuthorities());
		return result;
	}

	public void addStamp() throws ModuleException {
		Authentication authentication = getContext().getAuthentication();
		String id = authentication.getName();
		try {
			UriComponents uriComponents = newInstance().uri(getFindUserByIdURI(id)).buildAndExpand();

			ResponseEntity<UserRepresentation> response = restTemplate.exchange(uriComponents.toString(), GET, null,
					UserRepresentation.class);
			UserRepresentation userRepresentation = response.getBody();

			List<String> stamps = userRepresentation.getAttributes().get("stamps");
			if (stamps == null) {
				stamps = new LinkedList<String>();
				userRepresentation.getAttributes().put("stamps", stamps);
			}
			stamps.add(dateFormat.format(new Date()));
			HttpEntity<UserRepresentation> request = new HttpEntity<>(userRepresentation);
			restTemplate.exchange(uriComponents.toString(), PUT, request, UserRepresentation.class);
		} catch (Exception e) {
			String message = "Cannot find user by id " + id;
			throw new ModuleException(message, e);
		}
	}

	public URI getUsersURI() {
		return usersURI;
	}

	public URI getFindUserByIdURI(String id) {
		return usersURI.resolve(usersURI.getPath() + "/admin/realms/" + usersRealm + "/users" + "/" + id);
	}

	public String getUsersRealm() {
		return usersRealm;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
}
