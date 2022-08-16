package it.vige.labs.gc.bean.votingpapers;

import java.util.List;

import it.vige.labs.gc.bean.vote.Identifier;

public class VotingPaper extends Identifier {

	private int maxCandidates;

	private String type;

	private boolean disjointed;

	private String zone;

	private List<Group> groups;

	private List<Party> parties;

	private List<VotingDate> dates;

	public int getMaxCandidates() {
		return maxCandidates;
	}

	public void setMaxCandidates(int maxCandidates) {
		this.maxCandidates = maxCandidates;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isDisjointed() {
		return disjointed;
	}

	public void setDisjointed(boolean disjointed) {
		this.disjointed = disjointed;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Party> getParties() {
		return parties;
	}

	public void setParties(List<Party> parties) {
		this.parties = parties;
	}

	public List<VotingDate> getDates() {
		return dates;
	}

	public void setDates(List<VotingDate> dates) {
		this.dates = dates;
	}
}