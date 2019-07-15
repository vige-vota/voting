package it.vige.labs.gc.domain;

import java.util.List;

public class Party {
	
	private Group group;

	private List<Candidate> candidates;

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}
}
