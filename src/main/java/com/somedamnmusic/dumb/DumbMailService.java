package com.somedamnmusic.dumb;

import com.somedamnmusic.apis.MailService;

public class DumbMailService implements MailService {

	public void sendLoginEmail(String email, String token) {
		System.out.println(email+" : You've got some mail : http://localhost:8080/token/"+token);

	}

}
