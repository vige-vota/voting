package it.vige.labs.gc.result;

import java.util.ArrayList;
import java.util.List;

public class Group extends Electors {

	private List<Party> parties = new ArrayList<Party>();

	private boolean empty;

	public Group() {

	}

	public Group(it.vige.labs.gc.vote.VotingPaper votingPaper) {
		setElectors(getElectors() + 1);
		setId(votingPaper.getGroup().getId());
		parties.add(new Party(votingPaper.getParty()));
	}

	public List<Party> getParties() {
		return parties;
	}

	public void setParties(List<Party> parties) {
		this.parties = parties;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty(boolean empty) {
		this.empty = empty;
	}

}
