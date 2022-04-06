package it.vige.labs.gc.users;

import static it.vige.labs.gc.bean.votingpapers.VotingDate.DATE_FORMAT;
import static java.util.stream.Collectors.toList;
import static org.jboss.logging.Logger.getLogger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jboss.logging.Logger;
import org.keycloak.representations.idm.UserRepresentation;

public interface Converters {

	Logger log = getLogger(Converters.class);

	DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

	Function<UserRepresentation, User> UserRepresentationToUser = new Function<UserRepresentation, User>() {

		public User apply(UserRepresentation t) {
			User user = new User();
			user.setId(t.getUsername().toUpperCase());
			user.setName(t.getFirstName());
			user.setSurname(t.getLastName());
			Map<String, List<String>> attributes = t.getAttributes();
			if (attributes != null) {
				List<String> zones = attributes.get("zones");
				user.setZones(zones.get(0));
				List<String> stamps = attributes.get("stamps");
				if (stamps != null && !stamps.isEmpty()) {
					List<Date> dates = null;
					try {
						dates = stamps.parallelStream().map(e -> {
							Date date = null;
							try {
								date = dateFormat.parse(e);
							} catch (ParseException e1) {
								log.error(e1);
							}
							return date;
						}).collect(toList());
					} catch (Exception ex) {
						// It is done to resolve a testing bug on mac osx. Sometime parallelStream
						// doesn't return the results
						dates = stamps.stream().map(e -> {
							Date date = null;
							try {
								date = dateFormat.parse(e);
							} catch (ParseException e1) {
								log.error(e1);
							}
							return date;
						}).collect(toList());
					}
					user.setStamps(dates);
				}
			}

			return user;
		}
	};
}