package it.vige.labs.gc.bean.vote;

import java.util.List;

import it.vige.labs.gc.bean.votingpapers.VotingPapers;
import it.vige.labs.gc.users.User;

public class Vote {

	private List<VotingPaper> votingPapers;

	public Vote() {
	}

	public Vote(List<VotingPaper> votingPapers) {
		this.votingPapers = votingPapers;
	}

	public List<VotingPaper> getVotingPapers() {
		return votingPapers;
	}

	public void setVotingPapers(List<VotingPaper> votingPapers) {
		this.votingPapers = votingPapers;
	}

	public void validate(VotingPapers votingPapers, Boolean[] results, User user) {
		int i = 0;
		List<VotingPaper> votingPapersFromVote = getVotingPapers();
		if (votingPapersFromVote.size() <= votingPapers.getVotingPapers().size() && !votingPapersFromVote.isEmpty())
			for (VotingPaper votingPaper : votingPapersFromVote) {
				votingPaper.validate(i++, votingPapers, results, user);
			}
	}
}
