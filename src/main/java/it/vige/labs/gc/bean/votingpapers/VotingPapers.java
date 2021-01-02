package it.vige.labs.gc.bean.votingpapers;

import java.util.ArrayList;
import java.util.List;

public class VotingPapers {

	private State state;

	private List<VotingPaper> votingPapers = new ArrayList<VotingPaper>();

	public List<VotingPaper> getVotingPapers() {
		return votingPapers;
	}

	public void setVotingPapers(List<VotingPaper> votingPapers) {
		this.votingPapers = votingPapers;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
}
