package it.vige.labs.gc.bean.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.vige.labs.gc.bean.vote.Vote;

public class VotingPapers extends Electors {

	@JsonIgnore
	private Map<Integer, VotingPaper> mapVotingPapers = new HashMap<Integer, VotingPaper>();

	public VotingPapers() {

	}

	public VotingPapers(Vote vote) {
		add(vote);
	}

	public Map<Integer, VotingPaper> getMapVotingPapers() {
		return mapVotingPapers;
	}

	public Collection<VotingPaper> getVotingPapers() {
		return mapVotingPapers.values();
	}

	public void setMapVotingPapers(Map<Integer, VotingPaper> mapVotingPapers) {
		this.mapVotingPapers = mapVotingPapers;
	}

	public void add(Vote vote) {
		setElectors(getElectors() + 1);
		vote.getVotingPapers().forEach(e -> {
			if (!mapVotingPapers.containsKey(e.getId()))
				mapVotingPapers.put(e.getId(), new VotingPaper(e));
			else
				mapVotingPapers.get(e.getId()).add(e);
		});
	}

}
