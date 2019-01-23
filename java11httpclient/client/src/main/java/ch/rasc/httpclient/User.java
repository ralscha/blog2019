package ch.rasc.httpclient;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public class User {
	private final long id;

	private final String username;

	@JsonbCreator
	public User(@JsonbProperty("id") long id,
			@JsonbProperty("username") String username) {
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
