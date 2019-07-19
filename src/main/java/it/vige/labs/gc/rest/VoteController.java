package it.vige.labs.gc.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.vige.labs.gc.result.Messages;
import it.vige.labs.gc.vote.Vote;

@RestController
@CrossOrigin(origins = "*")
public class VoteController {

	@Autowired
	private Validator validator;

	@PostMapping(value = "/vote")
	public Messages vote(@RequestBody Vote vote) {
		Messages messages = validator.validate(vote);
		return messages;
	}
}
