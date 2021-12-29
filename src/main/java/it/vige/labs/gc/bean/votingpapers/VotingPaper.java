package it.vige.labs.gc.bean.votingpapers;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import it.vige.labs.gc.bean.vote.Identifier;

public class VotingPaper extends Identifier {

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date startingDate;

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date endingDate;

	private int maxCandidates;

	private boolean disjointed;

	private String zone;

	private List<Group> groups;

	private List<Party> parties;

	public Date getStartingDate() {
		return startingDate;
	}

	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}

	public Date getEndingDate() {
		return endingDate;
	}

	public void setEndingDate(Date endingDate) {
		this.endingDate = endingDate;
	}

	public int getMaxCandidates() {
		return maxCandidates;
	}

	public void setMaxCandidates(int maxCandidates) {
		this.maxCandidates = maxCandidates;
	}

	public boolean isDisjointed() {
		return disjointed;
	}

	public void setDisjointed(boolean disjointed) {
		this.disjointed = disjointed;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Party> getParties() {
		return parties;
	}

	public void setParties(List<Party> parties) {
		this.parties = parties;
	}

	public boolean dateOk() {
		Date date = new Date();
		return startingDate.compareTo(endingDate) < 0 && endingDate.compareTo(date) > 0;
	}
}