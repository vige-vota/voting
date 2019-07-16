package it.vige.labs.gc;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.vige.labs.gc.votingpapers.VotingPapers;

@SpringBootApplication
public class JavaAppApplication {

	public final static VotingPapers votingPapers = new VotingPapers();

	public static void main(String[] args) {
		SpringApplication.run(JavaAppApplication.class, args);
	}

	public static VotingPapers getVotingPapers() {
		if (votingPapers.getVotingPapers().size() == 0) {
			InputStream jsonStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream("mock/config-app.json");
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				VotingPapers votingPapersFromJson = objectMapper.readValue(jsonStream, VotingPapers.class);
				votingPapers.setVotingPapers(votingPapersFromJson.getVotingPapers());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return votingPapers;
	}
}
