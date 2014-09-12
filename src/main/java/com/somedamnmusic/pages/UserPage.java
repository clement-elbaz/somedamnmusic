package com.somedamnmusic.pages;

import org.apache.commons.lang3.StringUtils;

import com.somedamnmusic.apis.UserService;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.apis.exception.UnexplainableUserServiceException;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.session.Session;

public abstract class UserPage {
	private User currentUser;
	
	public UserPage(Session session, UserService userService) {
		try {
			if(StringUtils.isNotBlank(session.getUserId())) {
				this.currentUser = userService.getUserFromId(session.getUserId());
			}
		} catch (NoUserException e) {
			// TODO log
			e.printStackTrace();
		} catch (UnexplainableUserServiceException e) {
			// TODO log
			e.printStackTrace();
		}
	}
	
	public boolean isLogged() {
		return currentUser != null;
	}
	
	public User getCurrentUser() {
		return currentUser;
	}

}
