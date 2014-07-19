package com.somedamnmusic.pages;

import com.google.sitebricks.rendering.EmbedAs;

@EmbedAs("DisplayName")
public class DisplayName {
	private String firstname;
	private String lastname;

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

}
