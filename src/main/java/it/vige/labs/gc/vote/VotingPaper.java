package it.vige.labs.gc.vote;

public class VotingPaper extends Identifier {

	private Party party;
	
	private Group group;

	public VotingPaper() {
		super();
	}

	public VotingPaper(int id, Party party) {
		super(id);
		this.party = party;
	}

	public VotingPaper(int id, Party party, Group group) {
		super(id);
		this.party = party;
		this.group = group;
	}

	public Party getParty() {
		return party;
	}

	public void setParty(Party party) {
		this.party = party;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
}
