package it.vige.labs.gc.bean.vote;

import static java.util.stream.Collectors.toList;

import java.util.List;

import it.vige.labs.gc.bean.votingpapers.VotingDate;
import it.vige.labs.gc.bean.votingpapers.VotingPapers;
import it.vige.labs.gc.users.User;

public class VotingPaper extends Validation {

	private Party party;

	private Group group;

	public VotingPaper() {
		super();
	}

	public VotingPaper(int id) {
		super(id);
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

	public void validate(int i, VotingPapers votingPapers, Boolean[] results, User user) {
		if (group == null && party == null)
			results[i] = true;
		else {
			it.vige.labs.gc.bean.votingpapers.VotingPaper votingPaperFromJson = votingPapers.getVotingPapers()
					.parallelStream().filter(e -> e.getId() == id).collect(toList()).get(0);
			if (id == votingPaperFromJson.getId()) {
				List<it.vige.labs.gc.bean.votingpapers.Group> groups = votingPaperFromJson.getGroups();
				List<VotingDate> dates = votingPaperFromJson.getDates();
				if (!user.hasZone(votingPaperFromJson) || dates == null
						|| !dates.parallelStream().anyMatch(votingDate -> votingDate.dateOk(user)))
					results[i] = false;
				else {
					if (groups != null) {
						List<it.vige.labs.gc.bean.votingpapers.Party> parties = groups.parallelStream()
								.flatMap(e -> e.getParties().parallelStream()).collect(toList());
						if (group != null)
							group.validate(i, results, groups, parties, votingPaperFromJson, party);
						else if (!votingPaperFromJson.isDisjointed())
							results[i] = false;
						else if (validateExisting(parties, party, votingPaperFromJson.getMaxCandidates()))
							results[i] = true;
					}

					List<it.vige.labs.gc.bean.votingpapers.Party> parties = votingPaperFromJson.getParties();
					if (parties != null)
						results[i] = validateExisting(parties, party, votingPaperFromJson.getMaxCandidates());
				}
			}
		}
	}
}
