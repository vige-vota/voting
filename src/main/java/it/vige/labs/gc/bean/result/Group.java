package it.vige.labs.gc.bean.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Group extends Electors {

	@JsonIgnore
	private Map<Integer, Party> mapParties = new HashMap<Integer, Party>();

	private boolean empty;

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

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

	public void add(it.vige.labs.gc.bean.vote.VotingPaper votingPaper) {
		setElectors(getElectors() + 1);
		if (votingPaper.getGroup() != null)
			setId(votingPaper.getGroup().getId());
		if (votingPaper.getParty() != null) {
			if (votingPaper.getGroup() == null)
				setEmpty(true);
			if (!mapParties.containsKey(votingPaper.getParty().getId()))
				mapParties.put(votingPaper.getParty().getId(), new Party(votingPaper.getParty()));
			else
				mapParties.get(votingPaper.getParty().getId()).add(votingPaper.getParty());
		}
	}

}
