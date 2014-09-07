package com.somedamnmusic.pages;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Get;
import com.somedamnmusic.session.Session;

@At("/signout")
public class Signout {
	private final Session session;
	
	@Inject
	public Signout(Session session) {
		this.session = session;
	}
	
	@Get
	public String get() {
		this.session.setJustLoggedOut(true);
		return "/";
	}

}
