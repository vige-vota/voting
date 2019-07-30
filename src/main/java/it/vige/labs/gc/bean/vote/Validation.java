package it.vige.labs.gc.bean.vote;

import java.util.List;

public class Validation extends Identifier {

	public Validation() {
		super();
	}

	public Validation(int id) {
		super(id);
	}

	public boolean validateParty(List<it.vige.labs.gc.bean.votingpapers.Party> parties, Party party,
			int maxCandidates) {
		if (party != null)
			return party.validate(parties, maxCandidates);
		else
			return true;
	}
}
