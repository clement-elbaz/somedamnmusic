package com.somedamnmusic.jobs;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.protobuf.ByteString;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.MailService;
import com.somedamnmusic.database.UnexplainableDatabaseServiceException;
import com.somedamnmusic.mail.UnexplainableEmailServiceException;

public class LoginJob implements Runnable {
	
	final MailService mailService;
	final DatabaseService databaseService;
	final String email;
	
	@Inject
	public LoginJob(MailService mailService, final DatabaseService databaseService,  @Assisted String email) {
		this.mailService = mailService;
		this.databaseService = databaseService;
		this.email = email;
	}

	public void run() {
		String token;
		try {
			token = databaseService.getAvailablekey();
			databaseService.set(token, ByteString.copyFromUtf8(email));
			mailService.sendLoginEmail(email, token);
		} catch (UnexplainableEmailServiceException e) {
			e.printStackTrace(); // TODO log
		} catch (UnexplainableDatabaseServiceException e) {
			e.printStackTrace(); // TODO log
		}
	}
	
	public interface LoginJobFactory {
		LoginJob create(String email);
	}

}
