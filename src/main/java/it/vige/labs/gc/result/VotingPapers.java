package it.vige.labs.gc.result;

import java.util.ArrayList;
import java.util.List;

import it.vige.labs.gc.vote.Vote;

public class VotingPapers extends Electors {

	private List<VotingPaper> votingPapers = new ArrayList<VotingPaper>();

	public List<VotingPaper> getVotingPapers() {
		return votingPapers;
	}

	public void setVotingPapers(List<VotingPaper> votingPapers) {
		this.votingPapers = votingPapers;
	}
	
	public void add(Vote vote) {
		setElectors(getElectors() +1);
		vote.getVotingPapers().forEach(e -> {
			votingPapers.add(new VotingPaper(e));
		});
	}

}
