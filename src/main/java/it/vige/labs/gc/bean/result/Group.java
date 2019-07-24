package it.vige.labs.gc.bean.result;

import java.util.HashMap;
import java.util.Map;

public class Group extends Electors {

	private Map<Integer, Party> parties = new HashMap<Integer, Party>();

	private boolean empty;

	public Group() {

	}

	public Group(it.vige.labs.gc.bean.vote.VotingPaper votingPaper) {
		add(votingPaper);
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

	public void add(it.vige.labs.gc.bean.vote.VotingPaper votingPaper) {
		setElectors(getElectors() + 1);
		if (votingPaper.getGroup() != null)
			setId(votingPaper.getGroup().getId());
		if (votingPaper.getParty() != null) {
			if (votingPaper.getGroup() == null)
				setEmpty(true);
			if (!parties.containsKey(votingPaper.getParty().getId()))
				parties.put(votingPaper.getParty().getId(), new Party(votingPaper.getParty()));
			else
				parties.get(votingPaper.getParty().getId()).add(votingPaper.getParty());
		}
	}

}
