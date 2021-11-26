package it.vige.labs.gc.users;

import static java.util.Arrays.asList;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public class User implements Serializable {

	private static final long serialVersionUID = -6713889813860348323L;

	private String id;

	private String name;

	private String surname;

	private List<String> zones;

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

	public List<String> getZones() {
		return zones;
	}

	public void setZones(List<String> zones) {
		this.zones = zones;
	}

	public Collection<? extends GrantedAuthority> getRoles() {
		return roles;
	}

	public void setRoles(Collection<? extends GrantedAuthority> roles) {
		this.roles = roles;
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
