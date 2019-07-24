package it.vige.labs.gc.bean.vote;

import java.util.List;

public class Vote {

	private List<VotingPaper> votingPapers;
	
	public Vote() {
	}
	
	public Vote(List<VotingPaper> votingPapers) {
		this.votingPapers = votingPapers;
	}

	public List<VotingPaper> getVotingPapers() {
		return votingPapers;
	}

	public void setVotingPapers(List<VotingPaper> votingPapers) {
		this.votingPapers = votingPapers;
	}
}
