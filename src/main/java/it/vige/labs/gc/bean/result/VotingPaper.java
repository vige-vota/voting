package it.vige.labs.gc.bean.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class VotingPaper extends Electors {

	@JsonIgnore
	private Map<Integer, Group> mapGroups = new HashMap<Integer, Group>();

	@JsonIgnore
	private Map<Integer, Party> mapParties = new HashMap<Integer, Party>();

	private int blankPapers;

	public VotingPaper() {

	}

	public VotingPaper(it.vige.labs.gc.bean.vote.VotingPaper votingPaper) {
		add(votingPaper);
	}

	public int getBlankPapers() {
		return blankPapers;
	}

	public void setBlankPapers(int blankPapers) {
		this.blankPapers = blankPapers;
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

	public Map<Integer, Party> getMapParties() {
		return mapParties;
	}

	public Collection<Party> getParties() {
		return mapParties.values();
	}

	public void setMapParties(Map<Integer, Party> mapParties) {
		this.mapParties = mapParties;
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
		List<it.vige.labs.gc.bean.vote.Party> parties = votingPaper.getParties();
		if (parties != null && !parties.isEmpty()) {
			parties.forEach(party -> {
				int id = party.getId();
				if (!mapParties.containsKey(id))
					mapParties.put(id, new Party(party));
				else
					mapParties.get(id).add(party);
			});
		}
		if (group == null && (parties == null || parties.isEmpty()))
			blankPapers++;
	}
}
