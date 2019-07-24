package it.vige.labs.gc.result;

import java.util.HashMap;
import java.util.Map;

public class Group extends Electors {

	private Map<Integer, Party> parties = new HashMap<Integer, Party>();

	private boolean empty;

	public Group() {

	}

	public Group(it.vige.labs.gc.vote.VotingPaper votingPaper) {
		setElectors(getElectors() + 1);
		if (votingPaper.getGroup() != null)
			setId(votingPaper.getGroup().getId());
		Party pt = new Party(votingPaper.getParty());
		if (!parties.containsKey(pt.getId()))
			parties.put(pt.getId(), pt);
	}

	public Map<Integer, Party> getParties() {
		return parties;
	}

	public void setParties(Map<Integer, Party> parties) {
		this.parties = parties;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

}
