package ch.rasc.httpclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
	private final long id;

	private final String username;

	@JsonCreator
	public User(@JsonProperty("id") long id,
			@JsonProperty("username") String username) {
		this.id = id;
		this.username = username;
	}

	public long getId() {
		return this.id;
	}

	public String getUsername() {
		return this.username;
	}

	@Override
	public String toString() {
		return "User [id=" + this.id + ", username=" + this.username + "]";
	}

}
