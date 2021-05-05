package it.vige.labs.gc.users;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.keycloak.representations.idm.UserRepresentation;

public interface Converters {

	Function<UserRepresentation, User> UserRepresentationToUser = new Function<UserRepresentation, User>() {

		public User apply(UserRepresentation t) {
			User user = new User();
			user.setId(t.getUsername().toUpperCase());
			user.setName(t.getFirstName());
			user.setSurname(t.getLastName());
			Map<String, List<String>> attributes = t.getAttributes();
			if (attributes != null) {
				List<String> zones = attributes.get("zones");
				user.setZones(zones.parallelStream().map(zone -> parseInt(zone)).collect(toList()));
			}

			return user;
		}
	};
}