package com.somedamnmusic.pages;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.session.Session;

@At("/")
public class MainPage {
	private final Session session;
	
	@Inject
	public MainPage(Session session) {
		this.session = session;
	}
	
	public boolean isLogged() {
		return session.getUser() != null;
	}
	
	public User getUser() {
		return session.getUser();
	}

}
