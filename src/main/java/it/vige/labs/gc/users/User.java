package it.vige.labs.gc.users;

import static java.util.Arrays.asList;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public class User implements Serializable {

	private static final long serialVersionUID = -6713889813860348323L;

	private String id;

	private String name;

	private String surname;

	private String zones;

	private List<Date> stamps = new ArrayList<Date>();

	private Collection<? extends GrantedAuthority> roles;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getZones() {
		return zones;
	}

	public void setZones(String zones) {
		this.zones = zones;
	}

	public Collection<? extends GrantedAuthority> getRoles() {
		return roles;
	}

	public void setRoles(Collection<? extends GrantedAuthority> roles) {
		this.roles = roles;
	}

	public List<Date> getStamps() {
		return stamps;
	}

	public void setStamps(List<Date> stamps) {
		this.stamps = stamps;
	}

	public boolean hasZone() {
		return !getZones().isEmpty();
	}

	public boolean hasRole(String... role) {
		List<String> rolesTocompare = asList(role);
		return roles.parallelStream().anyMatch(r -> rolesTocompare.contains(r.getAuthority()));
	}

	public boolean hasZone(it.vige.labs.gc.bean.votingpapers.VotingPaper votingPaperFromJson) {
		return votingPaperFromJson.getZone() == null || getZones().contains(votingPaperFromJson.getZone());
	}

	public boolean hasVoted(Date date) {
		return stamps.parallelStream().filter(e -> new TimeIgnoringComparator().compare(e, date) == 0).findFirst()
				.orElse(null) != null;
	}

	private class TimeIgnoringComparator implements Comparator<Date> {
		public int compare(Date d1, Date d2) {
			Calendar c1 = getInstance();
			c1.setTime(d1);
			Calendar c2 = getInstance();
			c2.setTime(d2);
			if (c1.get(YEAR) != c2.get(YEAR))
				return c1.get(YEAR) - c2.get(YEAR);
			if (c1.get(MONTH) != c2.get(MONTH))
				return c1.get(MONTH) - c2.get(MONTH);
			return c1.get(DAY_OF_MONTH) - c2.get(DAY_OF_MONTH);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
