package it.vige.labs.gc.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Party extends Electors {

	private Map<Integer, Candidate> candidates = new HashMap<Integer, Candidate>();

	public Party() {

	}

	public Party(it.vige.labs.gc.vote.Party party) {
		if (party != null) {
			setElectors(getElectors() + 1);
			setId(party.getId());
			if (party.getCandidates() != null) {
				List<Candidate> cas = party.getCandidates().parallelStream().map(e -> new Candidate(e))
						.collect(Collectors.toList());
				cas.forEach(e -> {
					if (!candidates.containsKey(e.getId()))
						candidates.put(e.getId(), e);
				});
			}
		}
	}

	public Map<Integer, Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(Map<Integer, Candidate> candidates) {
		this.candidates = candidates;
	}

}
