package it.vige.labs.gc.votingpapers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.vige.labs.gc.vote.Identifier;

@JsonIgnoreProperties("name,image")
public class Candidate extends Identifier {

	private char sex;

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

}