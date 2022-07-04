package it.vige.labs.gc.rest;

import static it.vige.labs.gc.JavaAppApplication.TOPIC_NAME;
import static it.vige.labs.gc.rest.Validator.defaultMessage;
import static it.vige.labs.gc.rest.Validator.errorMessage;
import static it.vige.labs.gc.users.Authorities.ADMIN_ROLE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.vige.labs.gc.bean.VoteRequest;
import it.vige.labs.gc.bean.result.VotingPapers;
import it.vige.labs.gc.bean.result.Votings;
import it.vige.labs.gc.bean.vote.Vote;
import it.vige.labs.gc.messages.Messages;
import it.vige.labs.gc.users.Authorities;
import it.vige.labs.gc.users.User;
import it.vige.labs.gc.websocket.WebSocketVotingClient;

@RestController
@CrossOrigin(origins = "*")
public class VoteController {

	private VotingPapers result = new VotingPapers();

	@Autowired
	private WebSocketVotingClient webSocketClient;

	@Autowired
	private Authorities authorities;

	@Autowired
	private Validator validator;

	@PostMapping(value = "/vote")
	public Messages vote(@RequestBody Vote vote) throws Exception {
		User user = authorities.getUser();
		Messages messages = validator.validate(vote, user);
		if (messages.isOk()) {
			result.add(vote);
			authorities.addStamp();
			webSocketClient.getStompSession().send(TOPIC_NAME, getResult());
		}
		return messages;
	}

	@GetMapping(value = "/result")
	public Votings getResult() {
		Votings votings = new Votings();
		votings.getVotings().add(result);
		return votings;
	}

	@PostMapping(value = "/result")
	public Votings getResult(@RequestBody VoteRequest voteRequest) {
		Vote vote = voteRequest.getVote();
		VotingPapers votingPaper = voteRequest.getVotingPapers();
		votingPaper.add(vote);
		Votings votings = new Votings();
		votings.getVotings().add(votingPaper);
		return votings;
	}

	@GetMapping(value = "/resetVotingPapers")
	public Messages resetVotingPapers() {
		try {
			User user = authorities.getUser();
			if (user.hasRole(ADMIN_ROLE))
				validator.setVotingPapers(null);
			else
				return errorMessage;
		} catch (Exception ex) {
			return errorMessage;
		}
		return defaultMessage;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public void setAuthorities(Authorities authorities) {
		this.authorities = authorities;
	}
}
