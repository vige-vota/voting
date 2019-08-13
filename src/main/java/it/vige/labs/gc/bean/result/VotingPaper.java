package it.vige.labs.gc.bean.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VotingPaper extends Electors {

	@JsonIgnore
	private Map<Integer, Group> mapGroups = new HashMap<Integer, Group>();

	@JsonIgnore
	private Map<Integer, Party> mapParties = new HashMap<Integer, Party>();

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
		if (group != null) {
			int id = group.getId();
			if (!mapGroups.containsKey(id))
				mapGroups.put(id, new Group(votingPaper));
			else
				mapGroups.get(id).add(votingPaper);
		}
		it.vige.labs.gc.bean.vote.Party party = votingPaper.getParty();
		if (party != null) {
			int id = party.getId();
			if (!mapParties.containsKey(id))
				mapParties.put(id, new Party(party));
			else
				mapParties.get(id).add(party);
		}
	}
}
