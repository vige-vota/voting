package it.vige.labs.gc.result;

import java.util.ArrayList;
import java.util.List;

public class VotingPapers extends Electors {

	private List<VotingPaper> votingPapers = new ArrayList<VotingPaper>();

	public List<VotingPaper> getVotingPapers() {
		return votingPapers;
	}

	public void setVotingPapers(List<VotingPaper> votingPapers) {
		this.votingPapers = votingPapers;
	}

}
