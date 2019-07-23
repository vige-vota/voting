package it.vige.labs.gc.result;

import java.util.ArrayList;
import java.util.List;

public class VotingPaper extends Electors {

	private List<Group> groups = new ArrayList<Group>();

	public VotingPaper() {

	}

	public VotingPaper(it.vige.labs.gc.vote.VotingPaper votingPaper) {
		setElectors(getElectors() + 1);
		groups.add(new Group(votingPaper));
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}
