package it.vige.labs.gc.votingpapers;

import java.util.List;

import it.vige.labs.gc.vote.Identifier;

public class VotingPaper extends Identifier {
    
    private int maxCandidates;
	
	private boolean disjointed;
	
	private List<Group> groups;

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

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}