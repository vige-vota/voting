package it.vige.labs.gc.result;

public class Candidate extends Electors {

	public Candidate() {

	}

	public Candidate(it.vige.labs.gc.vote.Candidate candidate) {
		add(candidate);
	}

	public void add(it.vige.labs.gc.vote.Candidate candidate) {
		setElectors(getElectors() + 1);
		setId(candidate.getId());
	}
}
