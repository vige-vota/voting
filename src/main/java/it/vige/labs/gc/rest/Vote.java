package it.vige.labs.gc.rest;

import java.util.List;

import it.vige.labs.gc.domain.VotingPaper;

public class Vote {

	private List<VotingPaper> votingPapers;

	public List<VotingPaper> getVotingPapers() {
		return votingPapers;
	}

	public void setVotingPapers(List<VotingPaper> votingPapers) {
		this.votingPapers = votingPapers;
	}
}
