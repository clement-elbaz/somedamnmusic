package com.somedamnmusic.pages;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.somedamnmusic.apis.FeedService;
import com.somedamnmusic.apis.UserService;
import com.somedamnmusic.apis.exception.UnexplainableFeedServiceException;
import com.somedamnmusic.pages.DisplayFeed.FormattedFeed;
import com.somedamnmusic.session.Session;

@At("/all")
public class AllPage extends UserPage {
	private final FeedService feedService;

	@Inject
	public AllPage(FeedService feedService, Session session, UserService userService) {
		super(session, userService);
		this.feedService = feedService;
	}
	
	public FormattedFeed getFeed() {
		try {
			return feedService.getFeed(FeedService.PUBLIC_GLOBAL_FEED);
		} catch (UnexplainableFeedServiceException e) {
			// TODO log
			e.printStackTrace();
			return null;
		}
	}

}
