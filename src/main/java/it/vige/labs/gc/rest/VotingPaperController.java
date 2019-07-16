package it.vige.labs.gc.rest;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.vige.labs.gc.votingpapers.VotingPapers;

@RestController
public class VotingPaperController {

	public final static VotingPapers votingPapers = new VotingPapers();

	@GetMapping(value = "/votingPapers")
	public VotingPapers getVotingPapers() {
		return generateVotingPapers();
	}

	public static VotingPapers generateVotingPapers() {
		if (votingPapers.getVotingPapers().size() == 0) {
			InputStream jsonStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("mock/config-app.json");
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				VotingPapers votingPapersFromJson = objectMapper.readValue(jsonStream, VotingPapers.class);
				votingPapers.setVotingPapers(votingPapersFromJson.getVotingPapers());
				votingPapers.setAdmin(votingPapersFromJson.isAdmin());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return votingPapers;
	}
}
