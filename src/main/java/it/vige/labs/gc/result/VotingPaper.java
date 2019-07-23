package it.vige.labs.gc.result;

import java.util.ArrayList;
import java.util.List;

public class VotingPaper extends Electors {

	private List<Group> groups = new ArrayList<Group>();

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
}
