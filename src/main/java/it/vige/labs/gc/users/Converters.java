package it.vige.labs.gc.users;

import static it.vige.labs.gc.bean.votingpapers.VotingDate.DATE_FORMAT;
import static java.util.stream.Collectors.toList;
import static org.jboss.logging.Logger.getLogger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.jboss.logging.Logger;
import org.keycloak.representations.account.UserProfileAttributeMetadata;
import org.keycloak.representations.account.UserProfileMetadata;
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

	Function<UserRepresentation, org.keycloak.representations.account.UserRepresentation> UserRepresentationToAccount = new Function<UserRepresentation, org.keycloak.representations.account.UserRepresentation>() {

		public org.keycloak.representations.account.UserRepresentation apply(UserRepresentation t) {
			org.keycloak.representations.account.UserRepresentation account = new org.keycloak.representations.account.UserRepresentation();
			account.setId(t.getId());
			account.setUsername(t.getUsername());
			account.setFirstName(t.getFirstName());
			account.setLastName(t.getLastName());
			account.setEmail(t.getEmail());
			Boolean isEmailVerified = t.isEmailVerified();
			if (isEmailVerified != null)
				account.setEmailVerified(isEmailVerified);
			account.setAttributes(t.getAttributes());
			List<UserProfileAttributeMetadata> attributes = new ArrayList<UserProfileAttributeMetadata>();
			UserProfileMetadata userProfileMetadata = new UserProfileMetadata(attributes);
			account.setUserProfileMetadata(userProfileMetadata);
			UserProfileAttributeMetadata firstName = new UserProfileAttributeMetadata("firstName", "${firstname}", true,
					false, null, new HashMap<String, Map<String, Object>>());
			userProfileMetadata.getAttributes().add(firstName);
			UserProfileAttributeMetadata lasttName = new UserProfileAttributeMetadata("lastName", "${lastname}", true,
					false, null, new HashMap<String, Map<String, Object>>());
			userProfileMetadata.getAttributes().add(lasttName);
			Map<String, Map<String, Object>> emailValidators = new HashMap<String, Map<String, Object>>();
			Map<String, Object> emailValidatorParameters = new HashMap<String, Object>();
			emailValidatorParameters.put("ignore.empty.value", true);
			emailValidators.put("email", emailValidatorParameters);
			UserProfileAttributeMetadata email = new UserProfileAttributeMetadata("email", "${email}", true, false,
					null, emailValidators);
			userProfileMetadata.getAttributes().add(email);
			UserProfileAttributeMetadata username = new UserProfileAttributeMetadata("username", "${username}", true,
					false, null, new HashMap<String, Map<String, Object>>());
			userProfileMetadata.getAttributes().add(username);
			return account;
		}
	};
}