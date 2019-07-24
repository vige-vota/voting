package it.vige.labs.gc.bean.votingpapers;

import it.vige.labs.gc.bean.vote.Identifier;

public class Candidate extends Identifier {

	private char sex;

	public char getSex() {
		return sex;
	}

	public void setSex(char sex) {
		this.sex = sex;
	}

}