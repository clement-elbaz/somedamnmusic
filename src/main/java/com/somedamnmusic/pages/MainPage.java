package com.somedamnmusic.pages;

import java.util.List;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.somedamnmusic.apis.FeedService;
import com.somedamnmusic.apis.UserService;
import com.somedamnmusic.apis.exception.UnexplainableFeedServiceException;
import com.somedamnmusic.pages.DisplayFeed.FeedPost;
import com.somedamnmusic.session.Session;

@At("/")
public class MainPage extends UserPage {
	private final FeedService feedService;

	@Inject
	public MainPage(FeedService feedService, Session session, UserService userService) {
		super(session, userService);
		this.feedService = feedService;
	}

	public List<FeedPost> getUserFeed() {
		try {
			return feedService.getFeed(this.getCurrentUser().getWhatIFollowFeedId());
		} catch (UnexplainableFeedServiceException e) {
			// TODO log
			e.printStackTrace();
			return null;
		}
	}

	public List<FeedPost> getPublicFeed() {
		try {
			return feedService.getFeed(FeedService.PUBLIC_GLOBAL_FEED);
		} catch (UnexplainableFeedServiceException e) {
			// TODO log
			e.printStackTrace();
			return null;
		}
	}

}
