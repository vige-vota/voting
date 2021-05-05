package it.vige.labs.gc.bean.votingpapers;

import java.util.List;

import it.vige.labs.gc.bean.vote.Identifier;

public class VotingPaper extends Identifier {
    
    private int maxCandidates;
	
	private boolean disjointed;
    
    private int zone = -1;
	
	private List<Group> groups;
	
	private List<Party> parties;

	public int getMaxCandidates() {
		return maxCandidates;
	}

	public void setMaxCandidates(int maxCandidates) {
		this.maxCandidates = maxCandidates;
	}

	public boolean isDisjointed() {
		return disjointed;
	}

	public void setDisjointed(boolean disjointed) {
		this.disjointed = disjointed;
	}

	public int getZone() {
		return zone;
	}

	public void setZone(int zone) {
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
}