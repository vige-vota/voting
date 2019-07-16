package it.vige.labs.gc.rest;

import java.util.Arrays;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.vige.labs.gc.domain.Vote;
import it.vige.labs.gc.result.Message;
import it.vige.labs.gc.result.Messages;
import it.vige.labs.gc.result.Severity;

@RestController
public class VoteController {

	public final static String ok = "ok";

	public final static Messages defaultMessage = new Messages(
			Arrays.asList(new Message[] { new Message(Severity.message, ok, "all is ok") }));

	@PostMapping(value = "/vote")
	public Messages vote(@RequestBody Vote vote) {
		return defaultMessage;
	}
}
