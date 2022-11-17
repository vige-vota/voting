package it.vige.labs.gc.bean.vote;

import java.util.List;

public class Validation extends Identifier {

	public Validation() {
		super();
	}

	public Validation(int id) {
		super(id);
	}

	public boolean validateExisting(List<it.vige.labs.gc.bean.votingpapers.Party> parties, List<Party> partiesVote,
			int maxCandidates) {
		if (partiesVote != null && !partiesVote.isEmpty())
			return partiesVote.parallelStream().allMatch(party -> party.validate(parties, maxCandidates));
		else
			return true;
	}
}
