package it.vige.labs.gc.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VoteController {

	@PostMapping(value = "/vote")
	public Vote vote(@RequestBody Vote vote) {
		return vote;
	}
}
