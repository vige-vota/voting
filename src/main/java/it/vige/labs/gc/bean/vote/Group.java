package it.vige.labs.gc.bean.vote;

import static java.util.stream.Collectors.toList;

import java.util.List;

public class Group extends Validation {

	public Group() {
		super();
	}

	public Group(int id) {
		super(id);
	}

	public void validate(int i, Boolean[] results, List<it.vige.labs.gc.bean.votingpapers.Group> groups,
			List<it.vige.labs.gc.bean.votingpapers.Party> parties,
			it.vige.labs.gc.bean.votingpapers.VotingPaper votingPaperFromJson, List<Party> partiesVote) {
		if (!votingPaperFromJson.isDisjointed()) {
			List<it.vige.labs.gc.bean.votingpapers.Group> matchedGroups = groups.parallelStream()
					.filter(e -> e.getId() == id).collect(toList());
			if (!matchedGroups.isEmpty())
				parties = groups.parallelStream().filter(e -> e.getId() == id).collect(toList()).get(0).getParties();
			else
				results[i] = false;
		}
		if (groups.parallelStream().anyMatch(e -> e.getId() == id)
				&& validateExisting(parties, partiesVote, votingPaperFromJson.getMaxCandidates()))
			results[i] = true;
	}

}
