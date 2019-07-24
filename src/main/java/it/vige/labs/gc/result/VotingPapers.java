package it.vige.labs.gc.result;

import java.util.HashMap;
import java.util.Map;

import it.vige.labs.gc.vote.Vote;

public class VotingPapers extends Electors {

	private Map<Integer, VotingPaper> votingPapers = new HashMap<Integer, VotingPaper>();

	public VotingPapers() {

	}

	public VotingPapers(Vote vote) {
		add(vote);
	}

	public Map<Integer, VotingPaper> getVotingPapers() {
		return votingPapers;
	}

	public void setVotingPapers(Map<Integer, VotingPaper> votingPapers) {
		this.votingPapers = votingPapers;
	}

	public void add(Vote vote) {
		setElectors(getElectors() + 1);
		vote.getVotingPapers().forEach(e -> {
			if (!votingPapers.containsKey(e.getId()))
				votingPapers.put(e.getId(), new VotingPaper(e));
			else
				votingPapers.get(e.getId()).add(e);
		});
	}

}
