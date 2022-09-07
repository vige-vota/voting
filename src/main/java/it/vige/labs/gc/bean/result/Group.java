package it.vige.labs.gc.bean.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Group extends Electors {

	@JsonIgnore
	private Map<Integer, Party> mapParties = new HashMap<Integer, Party>();

	public Group() {

	}

	public Group(it.vige.labs.gc.bean.vote.VotingPaper votingPaper) {
		add(votingPaper);
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
		if (votingPaper.getGroup() != null)
			setId(votingPaper.getGroup().getId());
		List<it.vige.labs.gc.bean.vote.Party> parties = votingPaper.getParties();
		if (parties != null && !parties.isEmpty()) {
			parties.forEach(party -> {
				if (!mapParties.containsKey(party.getId()))
					mapParties.put(party.getId(), new Party(party));
				else
					mapParties.get(party.getId()).add(party);
			});
		}
	}

}
