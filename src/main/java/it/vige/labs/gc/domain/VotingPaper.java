package it.vige.labs.gc.domain;

public class VotingPaper extends Identifier {

	private Party party;

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}
}
