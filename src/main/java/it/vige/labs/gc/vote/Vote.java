package it.vige.labs.gc.vote;

import java.util.List;

public class Vote {

	private List<VotingPaper> votingPapers;
	
	public Vote(List<VotingPaper> votingPapers) {
		super();
		this.votingPapers = votingPapers;
	}

	public List<VotingPaper> getVotingPapers() {
		return votingPapers;
	}

	public void setVotingPapers(List<VotingPaper> votingPapers) {
		this.votingPapers = votingPapers;
	}
}
