package com.somedamnmusic.pages;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.protobuf.ByteString;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Get;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.exception.NoResultException;
import com.somedamnmusic.database.UnexplainableDatabaseServiceException;

@At("/signup/:token")
public class SignupPage {
	private final DatabaseService db;
	
	private String token;
	private String email;
	
	private boolean authentOK;
	
	@Inject
	public SignupPage(DatabaseService db) {
		this.db = db;
	}
	
	@Get
	public void get(@Named("token")String token) {
		this.authentOK = true;
		this.token = token;
		
		try {
			ByteString content = db.get(token);
			this.email = content.toStringUtf8();
		} catch (UnexplainableDatabaseServiceException e) {
			e.printStackTrace(); // TODO log
		} catch (NoResultException e) {
			authentOK = false;
		}
	}

	public String getToken() {
		return token;
	}

	public boolean isAuthentOK() {
		return authentOK;
	}

	public String getEmail() {
		return email;
	}

}
