package com.somedamnmusic.pages;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.somedamnmusic.apis.FeedService;
import com.somedamnmusic.apis.UserService;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.apis.exception.UnexplainableFeedServiceException;
import com.somedamnmusic.apis.exception.UnexplainableUserServiceException;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.pages.DisplayFeed.FeedPost;
import com.somedamnmusic.session.Session;

@At("/all")
public class AllPage {
	private final FeedService feedService;
	private User currentUser;

	@Inject
	public AllPage(FeedService feedService, Session session, UserService userService) {
		this.feedService = feedService;
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
	
	public User getUser() {
		return currentUser;
	}
	
	public List<FeedPost> getFeed() {
		try {
			return feedService.getFeed(FeedService.PUBLIC_GLOBAL_FEED);
		} catch (UnexplainableFeedServiceException e) {
			// TODO log
			e.printStackTrace();
			return null;
		}
	}

}
