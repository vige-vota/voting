package it.vige.labs.gc.votingpapers;

import java.util.List;

public class Group extends Identifier {

	private String image;
	
	private String subtitle;
	
	private List<Party> parties;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<Party> getParties() {
		return parties;
	}

	public void setParties(List<Party> parties) {
		this.parties = parties;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	
}
