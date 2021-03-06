package com.somedamnmusic.pages;

import com.google.sitebricks.rendering.EmbedAs;

@EmbedAs("DisplayName")
public class DisplayName {
	private String firstname;
	private String lastname;
	private String userId;

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
