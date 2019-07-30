package it.vige.labs.gc.bean.vote;

import java.util.List;

import it.vige.labs.gc.bean.votingpapers.VotingPapers;

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
	
	public void validate(Boolean[] results, VotingPapers votingPapers) {
		int i = 0;
		for (VotingPaper votingPaper : getVotingPapers()) {
			votingPaper.validate(votingPapers, i++, results);
		}
	}
}
