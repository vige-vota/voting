package it.vige.labs.gc.bean.vote;

import java.util.List;
import java.util.stream.Collectors;

public class Group extends Validation {

	public Group() {
		super();
	}

	public Group(int id) {
		super(id);
	}

	public List<it.vige.labs.gc.bean.votingpapers.Party> validate(int i, Boolean[] results,
			List<it.vige.labs.gc.bean.votingpapers.Group> groups, List<it.vige.labs.gc.bean.votingpapers.Party> parties,
			it.vige.labs.gc.bean.votingpapers.VotingPaper votingPaperFromJson, Party party) {
		if (!votingPaperFromJson.isDisjointed()) {
			List<it.vige.labs.gc.bean.votingpapers.Group> matchedGroups = groups.parallelStream()
					.filter(e -> e.getId() == id).collect(Collectors.toList());
			if (!matchedGroups.isEmpty())
				parties = groups.parallelStream().filter(e -> e.getId() == id).collect(Collectors.toList()).get(0)
						.getParties();
			else
				results[i] = false;
		}
		if (groups.parallelStream().anyMatch(e -> e.getId() == id)
				&& validateParty(parties, party, votingPaperFromJson.getMaxCandidates()))
			results[i] = true;
		return parties;
	}

}
