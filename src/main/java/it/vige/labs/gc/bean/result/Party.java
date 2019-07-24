package it.vige.labs.gc.bean.result;

import java.util.HashMap;
import java.util.Map;

public class Party extends Electors {

	private Map<Integer, Candidate> candidates = new HashMap<Integer, Candidate>();

	public Party() {

	}

	public Party(it.vige.labs.gc.bean.vote.Party party) {
		add(party);
	}

	public Map<Integer, Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(Map<Integer, Candidate> candidates) {
		this.candidates = candidates;
	}

	public void add(it.vige.labs.gc.bean.vote.Party party) {
		if (party != null) {
			setElectors(getElectors() + 1);
			setId(party.getId());
			if (party.getCandidates() != null) {
				party.getCandidates().forEach(e -> {
					if (!candidates.containsKey(e.getId()))
						candidates.put(e.getId(), new Candidate(e));
					else
						candidates.get(e.getId()).add(e);
				});
			}
		}
	}

}
