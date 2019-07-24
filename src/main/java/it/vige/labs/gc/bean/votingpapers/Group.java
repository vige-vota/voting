package it.vige.labs.gc.bean.votingpapers;

import java.util.List;

import it.vige.labs.gc.bean.vote.Identifier;

public class Group extends Identifier {

	private List<Party> parties;

	public List<Party> getParties() {
		return parties;
	}

	public void setParties(List<Party> parties) {
		this.parties = parties;
	}

}