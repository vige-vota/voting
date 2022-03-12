package it.vige.labs.gc.bean.votingpapers;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import it.vige.labs.gc.users.User;

public class VotingDate {

	public final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

	@JsonFormat(shape = STRING, pattern = DATE_FORMAT)
	private Date startingDate;

	@JsonFormat(shape = STRING, pattern = DATE_FORMAT)
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

	public boolean dateOk(User user) {
		Date date = new Date();
		return startingDate != null && endingDate != null && startingDate.compareTo(endingDate) < 0
				&& endingDate.compareTo(date) > 0 && !user.hasVoted(date);
	}

}
