package it.vige.labs.gc.result;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Party extends Electors {

	private List<Candidate> candidates = new ArrayList<Candidate>();

	public Party() {

	}

	public Party(it.vige.labs.gc.vote.Party party) {
		setElectors(getElectors() + 1);
		setId(party.getId());
		candidates
				.addAll(party.getCandidates().parallelStream().map(e -> new Candidate(e)).collect(Collectors.toList()));
	}

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}

}
