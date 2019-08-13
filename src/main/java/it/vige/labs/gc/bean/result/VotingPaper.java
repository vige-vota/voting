package it.vige.labs.gc.bean.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VotingPaper extends Electors {

	@JsonIgnore
	private Map<Integer, Group> mapGroups = new HashMap<Integer, Group>();

	public VotingPaper() {

	}

	public VotingPaper(it.vige.labs.gc.bean.vote.VotingPaper votingPaper) {
		add(votingPaper);
	}

	public Map<Integer, Group> getMapGroups() {
		return mapGroups;
	}

	public Collection<Group> getGroups() {
		return mapGroups.values();
	}

	public void setMapGroups(Map<Integer, Group> mapGroups) {
		this.mapGroups = mapGroups;
	}

	public void add(it.vige.labs.gc.bean.vote.VotingPaper votingPaper) {
		setElectors(getElectors() + 1);
		setId(votingPaper.getId());
		it.vige.labs.gc.bean.vote.Group group = votingPaper.getGroup();
		int id = 0;
		if (group != null)
			id = group.getId();
		if (!mapGroups.containsKey(id))
			mapGroups.put(id, new Group(votingPaper));
		else
			mapGroups.get(id).add(votingPaper);
	}
}
