package com.somedamnmusic.pages;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.sitebricks.rendering.EmbedAs;
import com.somedamnmusic.apis.UserService;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.apis.exception.UnexplainableUserServiceException;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.session.Session;

@EmbedAs("FollowButton")
public class FollowButton {
	private User currentUser;
	private String followedUserId;
	private String returnURL;
	
	@Inject
	public FollowButton(Session session, UserService userService) {
		try {
			this.currentUser = userService.getUserFromId(session.getUserId());
		} catch (NoUserException e) {
			// TODO log
			e.printStackTrace();
		} catch (UnexplainableUserServiceException e) {
			// TODO log
			e.printStackTrace();
		}
	}
	
	public String getFollowedUserId() {
		return followedUserId;
	}
	public void setFollowedUserId(String followedUserId) {
		this.followedUserId = followedUserId;
	}
	
	public boolean isFollowable() {
		return StringUtils.isNotBlank(followedUserId) && 
				currentUser != null
				&& !currentUser.getUserId().equals(followedUserId)
				&& !currentUser.getFollowingsList().contains(followedUserId);
	}
	
	public String getReturnURL() {
		return returnURL;
	}
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

}
