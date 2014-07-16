package com.somedamnmusic.apis;

public interface MailService {
	
	/**
	 * Send an email containing a URL with a token.
	 * By clicking on this URL, the user log in.
	 * 
	 * @param token
	 */
	void sendLoginEmail(String token);

}
