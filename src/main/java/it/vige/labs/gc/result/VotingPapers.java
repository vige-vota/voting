package it.vige.labs.gc.result;

import java.util.HashMap;
import java.util.Map;

import it.vige.labs.gc.vote.Vote;

public class VotingPapers extends Electors {

	private Map<Integer, VotingPaper> votingPapers = new HashMap<Integer, VotingPaper>();

	public Map<Integer, VotingPaper> getVotingPapers() {
		return votingPapers;
	}

	public void setVotingPapers(Map<Integer, VotingPaper> votingPapers) {
		this.votingPapers = votingPapers;
	}

	public void add(Vote vote) {
		setElectors(getElectors() + 1);
		vote.getVotingPapers().forEach(e -> {
			VotingPaper vt = new VotingPaper(e);
			if (!votingPapers.containsKey(vt.getId()))
				votingPapers.put(vt.getId(), vt);
		});
	}

}
