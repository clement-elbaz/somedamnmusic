package com.somedamnmusic.pages;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Get;
import com.somedamnmusic.apis.FeedService;
import com.somedamnmusic.apis.FollowService;
import com.somedamnmusic.apis.UserService;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.pages.DisplayFeed.FeedPost;
import com.somedamnmusic.session.Session;

@At("/profile/:userId")
public class ProfilePage {
	private final UserService userService;
	private final FeedService feedService;
	private final FollowService followService;
	private final Session session;
	
	private User user;
	private List<FeedPost> feed;
	
	@Inject
	public ProfilePage(UserService userService, FeedService feedService, FollowService followService, Session session) {
		this.userService = userService;
		this.feedService = feedService;
		this.followService = followService;
		this.session = session;
	}
	
	@Get
	public void get(@Named("userId")String userId) {
		User followedUser = this.session.getJustFollowedUser();
		if(followedUser != null) {
			this.session.setUser(followService.followUser(this.session.getUser(), followedUser));
			System.out.println(this.session.getUser().getFirstName()+" just followed "+followedUser.getFirstName());
		}
		
		try {
			user = userService.getUserFromId(userId);
			feed = feedService.getFeed(user.getWhatIPostFeedId());
		} catch (NoUserException e) {
			this.user = null;
		}
	}
	
	public boolean isLogged() {
		return this.session.getUser() != null;
	}
	
	public boolean isUserExists() {
		return this.user != null;
	}
	
	public User getUser() {
		return user;
	}
	
	public List<FeedPost> getFeed() {
		return feed;
	}
	
	public String getThisUserId() {
		if(session.getUser() != null) {
			return session.getUser().getUserId();
		}
		return null;
	}
	
	public String getProfileURL() {
		return "/profile/"+this.user.getUserId();
	}
	

}
