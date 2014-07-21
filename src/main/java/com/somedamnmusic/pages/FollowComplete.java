package com.somedamnmusic.pages;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Post;
import com.somedamnmusic.apis.UserService;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.session.Session;

@At("/follow")
public class FollowComplete {
	private final UserService userService;
	private final Session session;
	
	private User currentUser;
	private String followedUserId;
	private String returnURL;
	
	@Inject
	public FollowComplete(UserService userService, Session session) {
		this.userService = userService;
		this.session = session;
		try {
			this.currentUser = userService.getUserFromId(session.getUserId());
		} catch (NoUserException e) {
			// TODO log
		}
	}
	
	@Post
	public String post() {
		if(validate()) {
			try {
				User followedUser = userService.getUserFromId(followedUserId);
				session.setJustFollowedUser(followedUser);
			} catch (NoUserException e) {
				// do nothing
			}
		}
		
		return returnURL;
	}
	
	private boolean validate() {
		return currentUser != null && StringUtils.isNotBlank(followedUserId)
				&& !currentUser.getUserId().equals(followedUserId);
	}

	public String getFollowedUserId() {
		return followedUserId;
	}
	public void setFollowedUserId(String followedUserId) {
		this.followedUserId = followedUserId;
	}
	
	public String getReturnURL() {
		return returnURL;
	}
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

}
