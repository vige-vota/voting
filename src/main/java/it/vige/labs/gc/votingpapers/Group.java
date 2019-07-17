package it.vige.labs.gc.votingpapers;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.vige.labs.gc.vote.Identifier;

@JsonIgnoreProperties("name,subtitle,image")
public class Group extends Identifier {

	private List<Party> parties;

	public List<Party> getParties() {
		return parties;
	}

	public void setParties(List<Party> parties) {
		this.parties = parties;
	}

}