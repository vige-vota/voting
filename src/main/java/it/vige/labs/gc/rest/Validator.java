package it.vige.labs.gc.rest;

import java.util.Arrays;
import java.util.List;
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
import it.vige.labs.gc.vote.VotingPaper;
import it.vige.labs.gc.votingpapers.Candidate;
import it.vige.labs.gc.votingpapers.Group;
import it.vige.labs.gc.votingpapers.Party;
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

	public final static Messages errorMessage = new Messages(false,
			Arrays.asList(new Message[] { new Message(Severity.error, "Generic error", "Validation not ok") }));

	private VotingPapers votingPapers;

	public Messages validate(Vote vote) {
		VotingPapers votingPapers = getVotingPapers();
		Boolean[] results = new Boolean[vote.getVotingPapers().size()];
		for (int i = 0; i < results.length; i++)
			results[i] = false;
		int i = 0;
		for (VotingPaper votingPaper : vote.getVotingPapers()) {
			it.vige.labs.gc.votingpapers.VotingPaper votingPaperFromJson = votingPapers.getVotingPapers()
					.parallelStream().filter(e -> e.getId() == votingPaper.getId()).collect(Collectors.toList()).get(0);
			if (votingPaper.getId() == votingPaperFromJson.getId()) {
				List<Group> groups = votingPapers.getVotingPapers().parallelStream()
						.filter(e -> e.getId() == votingPaper.getId()).collect(Collectors.toList()).get(0).getGroups();

				List<Party> parties = groups.get(0).getParties();
				if (votingPaper.getGroup() != null) {
					if (!votingPaperFromJson.isDisjointed()) {
						List<Group> matchedGroups = groups.parallelStream()
								.filter(e -> e.getId() == votingPaper.getGroup().getId()).collect(Collectors.toList());
						if (!matchedGroups.isEmpty())
							parties = groups.parallelStream().filter(e -> e.getId() == votingPaper.getGroup().getId())
									.collect(Collectors.toList()).get(0).getParties();
						else
							results[i] = false;
					} else
						parties = groups.parallelStream().flatMap(e -> e.getParties().parallelStream())
								.collect(Collectors.toList());
					if (groups.parallelStream().anyMatch(e -> e.getId() == votingPaper.getGroup().getId())
							&& validateParty(parties, votingPaper.getParty(), votingPaperFromJson.getMaxCandidates()))
						results[i] = true;
				} else if (validateParty(parties, votingPaper.getParty(), votingPaperFromJson.getMaxCandidates()))
					results[i] = true;
			}
			i++;
		}
		return Arrays.stream(results).allMatch(e -> e == true) ? defaultMessage : errorMessage;
	}

	private boolean validateParty(List<Party> parties, it.vige.labs.gc.vote.Party party, int maxCandidates) {
		boolean result = parties.parallelStream().anyMatch(e -> e.getId() == party.getId());
		if (result && party.getCandidates() != null && !party.getCandidates().isEmpty()) {
			if (party.getCandidates().size() <= maxCandidates) {
				List<Candidate> candidates = parties.parallelStream().filter(e -> e.getId() == party.getId())
						.flatMap(e -> e.getCandidates().parallelStream()).collect(Collectors.toList());
				result = party.getCandidates().parallelStream()
						.allMatch(e -> candidates.parallelStream().anyMatch(f -> e.getId() == f.getId()));
				if (party.getCandidates().size() != 1) {
					List<Character> sexCandidates = candidates.parallelStream()
							.filter(e -> party.getCandidates().parallelStream().anyMatch(f -> f.getId() == e.getId()))
							.map(f -> f.getSex()).collect(Collectors.toList());
					result = sexCandidates.toString().matches("^(?=.*M)(?=.*F).+$");
				} else
					result = true;
			} else
				result = false;
		}
		return result;
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
