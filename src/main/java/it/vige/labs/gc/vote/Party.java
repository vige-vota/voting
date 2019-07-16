package it.vige.labs.gc.vote;

import java.util.List;

public class Party extends Identifier {

	private List<Candidate> candidates;

	public Party() {
		super();
	}

	public Party(int id) {
		super(id);
	}

	public Party(int id, List<Candidate> candidates) {
		this(id);
		this.candidates = candidates;
	}

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}
}
