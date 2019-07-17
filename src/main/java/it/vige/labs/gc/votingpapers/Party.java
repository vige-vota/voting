package it.vige.labs.gc.votingpapers;

import java.util.List;

import it.vige.labs.gc.vote.Identifier;

public class Party extends Identifier {

	private List<Candidate> candidates;

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}
}