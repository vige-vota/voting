package it.vige.labs.gc.rest;

import org.springframework.stereotype.Component;

import it.vige.labs.gc.result.Group;
import it.vige.labs.gc.result.Party;
import it.vige.labs.gc.result.VotingPapers;
import it.vige.labs.gc.vote.Candidate;
import it.vige.labs.gc.vote.Vote;
import it.vige.labs.gc.vote.VotingPaper;

@Component
public class UpdateResult {

	public void execute(Vote vote, VotingPapers votingPapers) {
		votingPapers.setElectors(votingPapers.getElectors() + 1);
		for (VotingPaper voteVotingPaper : vote.getVotingPapers()) {
			if (votingPapers.getVotingPapers().parallelStream().filter(e -> e.getId() == voteVotingPaper.getId())
					.count() == 0) {
				it.vige.labs.gc.result.VotingPaper resultVotingPaper = new it.vige.labs.gc.result.VotingPaper();
				resultVotingPaper.setId(voteVotingPaper.getId());
				votingPapers.getVotingPapers().add(resultVotingPaper);
			}
			for (it.vige.labs.gc.result.VotingPaper resultVotingPaper : votingPapers.getVotingPapers()) {
				if (resultVotingPaper.getId() == voteVotingPaper.getId()) {
					resultVotingPaper.setId(voteVotingPaper.getId());
					resultVotingPaper.setElectors(resultVotingPaper.getElectors() + 1);
					if (voteVotingPaper.getGroup() == null && voteVotingPaper.getParty() != null) {
						Group resultGroup = new Group();
						resultGroup.setEmpty(true);
						resultVotingPaper.getGroups().add(resultGroup);
					} else if (voteVotingPaper.getGroup() != null && resultVotingPaper.getGroups().parallelStream()
							.filter(e -> e.getId() == voteVotingPaper.getGroup().getId()).count() == 0) {
						Group resultGroup = new Group();
						resultGroup.setId(voteVotingPaper.getGroup().getId());
						resultVotingPaper.getGroups().add(resultGroup);
					}
					for (Group resultGroup : resultVotingPaper.getGroups()) {
						if (voteVotingPaper.getGroup() != null
								&& resultGroup.getId() == voteVotingPaper.getGroup().getId() && !resultGroup.isEmpty())
							resultGroup.setElectors(resultGroup.getElectors() + 1);
						if (voteVotingPaper.getParty() != null) {
							if (resultGroup.getParties().parallelStream()
									.filter(e -> e.getId() == voteVotingPaper.getParty().getId()).count() == 0) {
								Party resultParty = new Party();
								resultParty.setId(voteVotingPaper.getParty().getId());
								resultGroup.getParties().add(resultParty);
							}
							for (Party resultParty : resultGroup.getParties()) {
								if (resultParty.getId() == voteVotingPaper.getParty().getId())
									resultParty.setElectors(resultParty.getElectors() + 1);
								if (voteVotingPaper.getParty().getCandidates() != null)
									for (Candidate voteCandidate : voteVotingPaper.getParty().getCandidates()) {
										if (resultParty.getCandidates().parallelStream()
												.filter(e -> e.getId() == voteCandidate.getId()).count() == 0) {
											it.vige.labs.gc.result.Candidate resultCandidate = new it.vige.labs.gc.result.Candidate();
											resultCandidate.setId(voteCandidate.getId());
											resultParty.getCandidates().add(resultCandidate);
										}
										for (it.vige.labs.gc.result.Candidate resultCandidate : resultParty
												.getCandidates())
											if (resultCandidate.getId() == voteCandidate.getId())
												resultCandidate.setElectors(resultCandidate.getElectors() + 1);
									}
							}
						}
					}
				}
			}
		}
	}
}
