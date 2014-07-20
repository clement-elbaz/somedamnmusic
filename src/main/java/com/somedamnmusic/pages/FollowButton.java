package com.somedamnmusic.pages;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.sitebricks.rendering.EmbedAs;
import com.somedamnmusic.session.Session;

@EmbedAs("FollowButton")
public class FollowButton {
	private final Session session;
	private String followedUserId;
	private String returnURL;
	
	@Inject
	public FollowButton(Session session) {
		this.session = session;
	}
	
	public String getFollowedUserId() {
		return followedUserId;
	}
	public void setFollowedUserId(String followedUserId) {
		this.followedUserId = followedUserId;
	}
	
	public boolean isFollowable() {
		return StringUtils.isNotBlank(followedUserId) && 
				session.getUser() != null
				&& !session.getUser().getUserId().equals(followedUserId)
				&& !session.getUser().getFollowingsList().contains(followedUserId);
	}
	
	public String getReturnURL() {
		return returnURL;
	}
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

}
