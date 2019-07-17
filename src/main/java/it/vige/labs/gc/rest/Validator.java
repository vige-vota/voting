package it.vige.labs.gc.rest;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import it.vige.labs.gc.result.Message;
import it.vige.labs.gc.result.Messages;
import it.vige.labs.gc.result.Severity;
import it.vige.labs.gc.vote.Vote;
import it.vige.labs.gc.votingpapers.VotingPapers;

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
			Arrays.asList(new Message[] { new Message(Severity.message, ok, "all is ok") }));

	private VotingPapers votingPapers;

	public Messages validate(Vote vote) {
		VotingPapers votingPapers = getVotingPapers();
		if (votingPapers.getVotingPapers().parallelStream()
				.filter(e -> e.getId() == vote.getVotingPapers().get(0).getId()).collect(Collectors.toList())
				.size() > 0)
			return defaultMessage;
		else
			return new Messages(false,
					Arrays.asList(new Message[] { new Message(Severity.error, "Id not ok", "Id doesn't exist") }));
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
