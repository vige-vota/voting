package it.vige.labs.gc.result;

import java.util.ArrayList;
import java.util.List;

public class Party extends Electors {

	private List<Candidate> candidates = new ArrayList<Candidate>();

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}

}
