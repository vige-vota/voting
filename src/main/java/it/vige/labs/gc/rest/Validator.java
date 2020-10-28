package it.vige.labs.gc.rest;

import java.util.Arrays;

import org.keycloak.adapters.springsecurity.client.KeycloakRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import it.vige.labs.gc.bean.vote.Vote;
import it.vige.labs.gc.bean.votingpapers.VotingPapers;
import it.vige.labs.gc.messages.Message;
import it.vige.labs.gc.messages.Messages;
import it.vige.labs.gc.messages.Severity;

@Component
public class Validator {

	public final static String ok = "ok";

	@Autowired
	private KeycloakRestTemplate restTemplate;

	@Value("${votingpapers.scheme}")
	private String votingpapersScheme;

	@Value("${votingpapers.host}")
	private String votingpapersHost;

	@Value("${votingpapers.port}")
	private int votingpapersPort;

	public final static Messages defaultMessage = new Messages(true,
			Arrays.asList(new Message[] { new Message(Severity.message, ok, "all is ok") }));

	public final static Messages errorMessage = new Messages(false,
			Arrays.asList(new Message[] { new Message(Severity.error, "Generic error", "Validation not ok") }));

	private VotingPapers votingPapers;

	public Messages validate(Vote vote) {
		VotingPapers votingPapers = getVotingPapers();
		Boolean[] results = new Boolean[vote.getVotingPapers().size()];
		initResults(results);
		vote.validate(results, votingPapers);
		return Arrays.stream(results).allMatch(e -> e == true) ? defaultMessage : errorMessage;
	}

	private void initResults(Boolean[] results) {
		for (int i = 0; i < results.length; i++)
			results[i] = false;

	}

	private VotingPapers getVotingPapers() {
		if (votingPapers == null) {
			UriComponents uriComponents = UriComponentsBuilder.newInstance().scheme(votingpapersScheme)
					.host(votingpapersHost).port(votingpapersPort).path("/votingPapers").buildAndExpand();

			ResponseEntity<VotingPapers> response = restTemplate.exchange(uriComponents.toString(), HttpMethod.GET,
					null, VotingPapers.class);
			votingPapers = response.getBody();
		}
		return votingPapers;
	}
}
