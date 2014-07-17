package com.somedamnmusic.pages;

import java.util.List;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.somedamnmusic.apis.FeedService;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.pages.DisplayFeed.FeedPost;
import com.somedamnmusic.session.Session;

@At("/")
public class MainPage {
	private final FeedService feedService;
	private final Session session;
	
	@Inject
	public MainPage(FeedService feedService, Session session) {
		this.feedService = feedService;
		this.session = session;
	}
	
	public boolean isLogged() {
		return session.getUser() != null;
	}
	
	public User getUser() {
		return session.getUser();
	}
	
	public List<FeedPost> getUserFeed() {
		return feedService.getFeed(session.getUser().getWhatIFollowFeedId());
	}
	
	public List<FeedPost> getPublicFeed() {
		return feedService.getFeed(FeedService.PUBLIC_GLOBAL_FEED);
	}

}
