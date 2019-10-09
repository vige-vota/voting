package it.vige.labs.gc.bean.result;

import java.util.ArrayList;
import java.util.List;

public class Votings {

	private List<VotingPapers> votings = new ArrayList<VotingPapers>();

	public List<VotingPapers> getVotings() {
		return votings;
	}

	public void setVotings(List<VotingPapers> votings) {
		this.votings = votings;
	}
}
