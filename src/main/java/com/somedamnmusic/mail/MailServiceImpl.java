package com.somedamnmusic.mail;

import java.util.Arrays;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.somedamnmusic.apis.MailService;

public class MailServiceImpl implements MailService {
	private final String emailUserName;
	private final String emailPassword;
	
	@Inject
	public MailServiceImpl(@Named("email.user")String emailUserName, @Named("email.password")String emailPassword) {
		this.emailUserName = emailUserName;
		this.emailPassword = emailPassword;
	}

	@Override
	public void sendLoginEmail(String email, String token) throws UnexplainableEmailServiceException {
		try {
			SimpleEmail simpleEmail = new SimpleEmail();
			
			simpleEmail.setSubject("somedamnmusic.com - login");
			simpleEmail.setMsg("Hello, <br/><br/>Use this link to log in somedamnmusic.com : http://somedamnmusic.com/token/"+token);
			
			simpleEmail.setAuthentication(emailUserName, emailPassword);
			
			simpleEmail.setHostName("smtp.somedamnmusic.com");
			simpleEmail.setFrom("noreply@somedamnmusic.com");
			
			simpleEmail.setTo(Arrays.asList(new InternetAddress(email)));
			
			simpleEmail.send();
		} catch(EmailException e) {
			throw new UnexplainableEmailServiceException(e);
		} catch (AddressException e) {
			throw new UnexplainableEmailServiceException(e);
		}
		
	}

}
