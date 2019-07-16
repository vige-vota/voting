package it.vige.labs.gc.rest;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import it.vige.labs.gc.result.Message;
import it.vige.labs.gc.result.Messages;
import it.vige.labs.gc.result.Severity;
import it.vige.labs.gc.vote.Vote;
import it.vige.labs.gc.votingpapers.VotingPapers;

@Component
public class Validator {

	public final static String ok = "ok";

	public final static Messages defaultMessage = new Messages(true,
			Arrays.asList(new Message[] { new Message(Severity.message, ok, "all is ok") }));

	public Messages validate(Vote vote) {
		VotingPapers votingPapers = VotingPaperController.generateVotingPapers();
		if (votingPapers.getVotingPapers().parallelStream()
				.filter(e -> e.getId() == vote.getVotingPapers().get(0).getId()).collect(Collectors.toList())
				.size() > 0)
			return defaultMessage;
		else
			return new Messages(false,
					Arrays.asList(new Message[] { new Message(Severity.error, "Id not ok", "Id doesn't exist") }));
	}
}
