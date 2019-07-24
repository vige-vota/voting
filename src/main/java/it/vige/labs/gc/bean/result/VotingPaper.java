package it.vige.labs.gc.bean.result;

import java.util.HashMap;
import java.util.Map;

public class VotingPaper extends Electors {

	private Map<Integer, Group> groups = new HashMap<Integer, Group>();

	public VotingPaper() {

	}

	public VotingPaper(it.vige.labs.gc.bean.vote.VotingPaper votingPaper) {
		add(votingPaper);
	}

	public Map<Integer, Group> getGroups() {
		return groups;
	}

	public void setGroups(Map<Integer, Group> groups) {
		this.groups = groups;
	}

	public void add(it.vige.labs.gc.bean.vote.VotingPaper votingPaper) {
		setElectors(getElectors() + 1);
		setId(votingPaper.getId());
		it.vige.labs.gc.bean.vote.Group group = votingPaper.getGroup();
		if (group == null && votingPaper.getParty() != null)
			groups.put(0, new Group(votingPaper));
		else if (!groups.containsKey(group.getId()))
			groups.put(group.getId(), new Group(votingPaper));
		else
			groups.get(group.getId()).add(votingPaper);
	}
}
