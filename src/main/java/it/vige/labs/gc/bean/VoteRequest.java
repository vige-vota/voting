package it.vige.labs.gc.bean;

import it.vige.labs.gc.bean.result.VotingPapers;
import it.vige.labs.gc.bean.vote.Vote;

public class VoteRequest {

	private Vote vote;

	private VotingPapers votingPapers;

	public VoteRequest() {

	}

	public VoteRequest(Vote vote, VotingPapers votingPapers) {
		this.vote = vote;
		this.votingPapers = votingPapers;
	}

	public Vote getVote() {
		return vote;
	}

	public void setVote(Vote vote) {
		this.vote = vote;
	}

	public VotingPapers getVotingPapers() {
		return votingPapers;
	}

	public void setVotingPapers(VotingPapers votingPapers) {
		this.votingPapers = votingPapers;
	}
}
