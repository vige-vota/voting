package it.vige.labs.gc.bean.vote;

import java.util.List;
import java.util.stream.Collectors;

public class Party extends Identifier {

	private List<Candidate> candidates;

	public Party() {
		super();
	}

	public Party(int id) {
		super(id);
	}

	public Party(int id, List<Candidate> candidates) {
		this(id);
		this.candidates = candidates;
	}

	public List<Candidate> getCandidates() {
		return candidates;
	}

	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}

	public boolean validate(List<it.vige.labs.gc.bean.votingpapers.Party> parties, int maxCandidates) {
		boolean result = parties.parallelStream().anyMatch(e -> e.getId() == id);
		if (result && getCandidates() != null && !getCandidates().isEmpty()) {
			if (getCandidates().size() <= maxCandidates) {
				List<it.vige.labs.gc.bean.votingpapers.Candidate> candidates = parties.parallelStream()
						.filter(e -> e.getId() == id).flatMap(e -> e.getCandidates().parallelStream())
						.collect(Collectors.toList());
				result = getCandidates().parallelStream()
						.allMatch(e -> candidates.parallelStream().anyMatch(f -> e.getId() == f.getId()));
				if (getCandidates().size() != 1) {
					List<Character> sexCandidates = candidates.parallelStream()
							.filter(e -> getCandidates().parallelStream().anyMatch(f -> f.getId() == e.getId()))
							.map(f -> f.getSex()).collect(Collectors.toList());
					result = sexCandidates.toString().matches("^(?=.*M)(?=.*F).+$");
				} else
					result = true;
			} else
				result = false;
		}
		return result;
	}
}
