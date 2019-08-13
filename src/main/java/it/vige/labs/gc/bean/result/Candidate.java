package it.vige.labs.gc.bean.result;

public class Candidate extends Votes {

	public Candidate() {

	}

	public Candidate(it.vige.labs.gc.bean.vote.Candidate candidate) {
		add(candidate);
	}

	public void add(it.vige.labs.gc.bean.vote.Candidate candidate) {
		setVotes(getVotes() + 1);
		setId(candidate.getId());
	}
}
