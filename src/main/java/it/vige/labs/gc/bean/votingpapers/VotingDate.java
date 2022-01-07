package it.vige.labs.gc.bean.votingpapers;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class VotingDate {

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date startingDate;

	@JsonFormat(shape = STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date endingDate;

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

	public boolean dateOk() {
		Date date = new Date();
		return startingDate != null && endingDate != null && startingDate.compareTo(endingDate) < 0
				&& endingDate.compareTo(date) > 0;
	}

}
