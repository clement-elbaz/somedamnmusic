package com.somedamnmusic.pages;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Post;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.session.Session;

@At("/signupcomplete")
public class SignupComplete {
	private final DatabaseService db;
	private final Session session;
	
	private String token;
	private String firstname;
	private String lastname;
	
	@Inject
	public SignupComplete(DatabaseService db, Session session) {
		this.db = db;
		this.session = session;
	}
	
	@Post
	public String post() {
		if(!validate()) {
			return "http://you.little.hacker.com";
		}
		ByteString content = db.get(token);
		
		if(content != null) {
			String email = content.toStringUtf8();
			
			User.Builder newUser = User.newBuilder();
			
			newUser.setEmail(email);
			newUser.setFirstName(firstname);
			newUser.setLastName(lastname);
			
			User newUserCompleted = newUser.build();
			
			db.set(newUserCompleted.getEmail(), newUserCompleted.toByteString());
			session.setUser(newUserCompleted);
			db.remove(token);
			
			return "/";
		}
		
		return "/signupcomplete";
	}
	
	private boolean validate() {
		return StringUtils.isNotBlank(firstname)
				&& StringUtils.isNotBlank(lastname)
				&& StringUtils.isNotBlank(token);
	}
	
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = StringEscapeUtils.escapeHtml4(token);
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = StringEscapeUtils.escapeHtml4(firstname);
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = StringEscapeUtils.escapeHtml4(lastname);
	}

}
