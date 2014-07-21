package com.somedamnmusic.pages;

import java.util.List;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.somedamnmusic.apis.FeedService;
import com.somedamnmusic.apis.UserService;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.pages.DisplayFeed.FeedPost;
import com.somedamnmusic.session.Session;

@At("/")
public class MainPage {
	private final FeedService feedService;
	private User currentUser;
	
	@Inject
	public MainPage(FeedService feedService, Session session, UserService userService) {
		this.feedService = feedService;
		try {
			this.currentUser = userService.getUserFromId(session.getUserId());
		} catch (NoUserException e) {
			// TODO log
		}
	}
	
	public boolean isLogged() {
		return currentUser != null;
	}
	
	public User getUser() {
		return currentUser;
	}
	
	public List<FeedPost> getUserFeed() {
		return feedService.getFeed(currentUser.getWhatIFollowFeedId());
	}
	
	public List<FeedPost> getPublicFeed() {
		return feedService.getFeed(FeedService.PUBLIC_GLOBAL_FEED);
	}

}
