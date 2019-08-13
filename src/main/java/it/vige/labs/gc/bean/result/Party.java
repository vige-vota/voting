package it.vige.labs.gc.bean.result;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Party extends Votes {

	@JsonIgnore
	private Map<Integer, Candidate> mapCandidates = new HashMap<Integer, Candidate>();

	public Party() {

	}

	public Party(it.vige.labs.gc.bean.vote.Party party) {
		add(party);
	}

	public Map<Integer, Candidate> getMapCandidates() {
		return mapCandidates;
	}

	public Collection<Candidate> getCandidates() {
		return mapCandidates.values();
	}

	public void setMapCandidates(Map<Integer, Candidate> mapCandidates) {
		this.mapCandidates = mapCandidates;
	}

	public void add(it.vige.labs.gc.bean.vote.Party party) {
		if (party != null) {
			setVotes(getVotes() + 1);
			setId(party.getId());
			if (party.getCandidates() != null) {
				party.getCandidates().forEach(e -> {
					if (!mapCandidates.containsKey(e.getId()))
						mapCandidates.put(e.getId(), new Candidate(e));
					else
						mapCandidates.get(e.getId()).add(e);
				});
			}
		}
	}

}
