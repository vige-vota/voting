package it.vige.labs.gc.result;

import java.util.HashMap;
import java.util.Map;

public class VotingPaper extends Electors {

	private Map<Integer, Group> groups = new HashMap<Integer, Group>();

	public VotingPaper() {

	}

	public VotingPaper(it.vige.labs.gc.vote.VotingPaper votingPaper) {
		setElectors(getElectors() + 1);
		setId(votingPaper.getId());
		Group gr = new Group(votingPaper);
		if (!groups.containsKey(gr.getId()))
			groups.put(gr.getId(), gr);
	}

	public Map<Integer, Group> getGroups() {
		return groups;
	}

	public void setGroups(Map<Integer, Group> groups) {
		this.groups = groups;
	}
}
