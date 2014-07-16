package com.somedamnmusic.pages;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.sitebricks.At;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.session.Session;

@At("/token/:token")
public class TokenPage {
	private final Session session;
	private final DatabaseService db;
	private boolean needSignup;
	
	@Inject
	public TokenPage(Session session, DatabaseService databaseService) {
		this.session = session;
		this.db = databaseService;
		this.needSignup = false;
	}
	
	public void get(@Named("token")String token) {
		ByteString emailBT = db.get(token);
		if(emailBT != null) {
			String email = emailBT.toStringUtf8();
			
			try {
				User user = User.parseFrom(db.get(email));
				
				session.setUser(user);
				System.out.println(user.getFirstName()+ " signed in ");
			} catch (InvalidProtocolBufferException e) {
				needSignup = true;
			}
			
		}
	}

	public boolean isNeedSignup() {
		return needSignup;
	}
}
