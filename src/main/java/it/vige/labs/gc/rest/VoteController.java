package it.vige.labs.gc.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.vige.labs.gc.JavaAppApplication;
import it.vige.labs.gc.result.Messages;
import it.vige.labs.gc.vote.Vote;
import it.vige.labs.gc.votingpapers.VotingPapers;

@RestController
public class VoteController {

	@Autowired
	private Validator validator;

	@PostMapping(value = "/vote")
	public Messages vote(@RequestBody Vote vote) {
		Messages messages = validator.validate(vote);
		return messages;
	}

	@GetMapping(value = "/votingPapers")
	public VotingPapers getVotingPapers() {
		return JavaAppApplication.getVotingPapers();
	}
}
