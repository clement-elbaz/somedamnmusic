package com.somedamnmusic.apis;

import com.somedamnmusic.mail.UnexplainableEmailServiceException;

public interface MailService {
	
	/**
	 * Send an email containing a URL with a token.
	 * By clicking on this URL, the user log in.
	 * 
	 * @param email
	 * @param token
	 */
	void sendLoginEmail(String email, String token) throws UnexplainableEmailServiceException;

}
