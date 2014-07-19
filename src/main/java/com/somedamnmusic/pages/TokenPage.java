package com.somedamnmusic.pages;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.protobuf.ByteString;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Get;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.UserService;
import com.somedamnmusic.apis.exception.DatabaseException;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.session.Session;

@At("/token/:token")
public class TokenPage {
	private final UserService userService;
	private final Session session;
	private final DatabaseService db;
	private boolean needSignup;

	@Inject
	public TokenPage(UserService userService, Session session, DatabaseService databaseService) {
		this.userService = userService;
		this.session = session;
		this.db = databaseService;
		this.needSignup = false;
	}

	@Get
	public String get(@Named("token")String token) {
		try {
			ByteString emailBT = db.get(token);

			if(emailBT != null) {
				String email = emailBT.toStringUtf8();
				
				User user = userService.getUserFromEmail(email);
				
				session.setUser(user);
				db.remove(token);
			}
		} catch (DatabaseException e1) {
			e1.printStackTrace(); // TODO log
		} catch (NoUserException e) {
			needSignup = true;
		}

		if(!needSignup) {
			return "/";
		} else {
			return "/signup/"+token;
		}

	}

	public boolean isNeedSignup() {
		return needSignup;
	}
}
