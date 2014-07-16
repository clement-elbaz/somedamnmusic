package com.somedamnmusic.pages;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.somedamnmusic.session.Session;

@At("/")
public class MainPage {
	
	@Inject
	public MainPage(Session session) {
		
	}

}
