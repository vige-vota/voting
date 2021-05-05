package it.vige.labs.gc.rest;

import static it.vige.labs.gc.bean.votingpapers.State.PREPARE;
import static it.vige.labs.gc.messages.Severity.error;
import static it.vige.labs.gc.messages.Severity.message;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;

import it.vige.labs.gc.bean.vote.Vote;
import it.vige.labs.gc.bean.votingpapers.VotingPapers;
import it.vige.labs.gc.messages.Message;
import it.vige.labs.gc.messages.Messages;

@Component
public class Validator {

	public final static String ok = "ok";

	@Autowired
	private RestTemplate restTemplate;

	@Value("${votingpapers.scheme}")
	private String votingpapersScheme;

	@Value("${votingpapers.host}")
	private String votingpapersHost;

	@Value("${votingpapers.port}")
	private int votingpapersPort;

	public final static Messages defaultMessage = new Messages(true,
			asList(new Message[] { new Message(message, ok, "all is ok") }));

	public final static Messages errorMessage = new Messages(false,
			asList(new Message[] { new Message(error, "Generic error", "Validation not ok") }));

	private VotingPapers votingPapers;

	public Messages validate(Vote vote) {
		VotingPapers votingPapers = getVotingPapers();
		if (votingPapers.getState() == PREPARE)
			return errorMessage;
		Boolean[] results = new Boolean[vote.getVotingPapers().size()];
		initResults(results);
		vote.validate(votingPapers, results);
		return stream(results).allMatch(e -> e == true) ? defaultMessage : errorMessage;
	}

	private void initResults(Boolean[] results) {
		for (int i = 0; i < results.length; i++)
			results[i] = false;

	}

	private VotingPapers getVotingPapers() {
		if (votingPapers == null) {
			UriComponents uriComponents = newInstance().scheme(votingpapersScheme).host(votingpapersHost)
					.port(votingpapersPort).path("/votingPapers").buildAndExpand();

			ResponseEntity<VotingPapers> response = restTemplate.exchange(uriComponents.toString(), GET, null,
					VotingPapers.class);
			votingPapers = response.getBody();
		}
		return votingPapers;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}
}
