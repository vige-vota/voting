package it.vige.labs.gc.bean.vote;

import static it.vige.labs.gc.bean.votingpapers.Type.REFERENDUM;
import static java.util.stream.Collectors.toList;

import java.util.List;

import it.vige.labs.gc.bean.votingpapers.VotingDate;
import it.vige.labs.gc.bean.votingpapers.VotingPapers;
import it.vige.labs.gc.users.User;

public class VotingPaper extends Validation {

	private List<Party> parties;

	private Group group;

	public VotingPaper() {
		super();
	}

	public VotingPaper(int id) {
		super(id);
	}

	public VotingPaper(int id, List<Party> parties) {
		super(id);
		this.parties = parties;
	}

	public VotingPaper(int id, List<Party> parties, Group group) {
		super(id);
		this.parties = parties;
		this.group = group;
	}

	public List<Party> getParties() {
		return parties;
	}

	public void setParties(List<Party> parties) {
		this.parties = parties;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public void validate(int i, VotingPapers votingPapers, Boolean[] results, User user) {
		if (group == null && (parties == null || parties.isEmpty()))
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
						boolean isReferendum = votingPaperFromJson.getType().equals(REFERENDUM.asString());
						List<it.vige.labs.gc.bean.votingpapers.Party> parties = groups.parallelStream()
								.flatMap(e -> e.getParties().parallelStream()).collect(toList());
						if (!isReferendum) {
							if (group != null)
								group.validate(i, results, groups, parties, votingPaperFromJson, this.getParties());
							else if (!votingPaperFromJson.isDisjointed())
								results[i] = false;
							else if (validateExisting(parties, this.getParties(),
									votingPaperFromJson.getMaxCandidates()))
								results[i] = true;
						} else if (group != null)
							results[i] = false;
						else if (validateExisting(parties, this.getParties(), votingPaperFromJson.getMaxCandidates()))
							results[i] = true;
					}

					List<it.vige.labs.gc.bean.votingpapers.Party> parties = votingPaperFromJson.getParties();
					if (parties != null)
						results[i] = validateExisting(parties, this.getParties(),
								votingPaperFromJson.getMaxCandidates());
				}
			}
		}
	}
}
