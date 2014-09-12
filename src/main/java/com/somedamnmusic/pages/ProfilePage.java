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
import com.somedamnmusic.apis.exception.UnexplainableFeedServiceException;
import com.somedamnmusic.apis.exception.UnexplainableUserServiceException;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.pages.DisplayFeed.FeedPost;
import com.somedamnmusic.session.Session;

@At("/profile/:userId")
public class ProfilePage extends UserPage {
	private final UserService userService;
	private final FeedService feedService;
	private final FollowService followService;
	private final Session session;

	private User user;
	private List<FeedPost> feed;

	@Inject
	public ProfilePage(UserService userService, FeedService feedService, FollowService followService, Session session) {
		super(session, userService);
		this.userService = userService;
		this.feedService = feedService;
		this.followService = followService;
		this.session = session;
	}

	@Get
	public void get(@Named("userId")String userId) {
		User followedUser = this.session.getJustFollowedUser();
		if(followedUser != null) {
			followService.followUser(this.getCurrentUser(), followedUser);
			System.out.println(this.getCurrentUser().getFirstName()+" just followed "+followedUser.getFirstName());
		}

		try {
			user = userService.getUserFromId(userId);
			feed = feedService.getFeed(user.getWhatIPostFeedId());
		} catch (NoUserException e) {
			this.user = null;
		} catch (UnexplainableUserServiceException e) {
			// TODO log
			e.printStackTrace();
		} catch (UnexplainableFeedServiceException e) {
			// TODO log
			e.printStackTrace();
		}
	}

	public boolean isUserExists() {
		return this.user != null;
	}
	
	public boolean isCurrentProfile() {
		if(!this.isLogged()) {
			return false;
		}
		if(this.user == null) {
			return false;
		}
		return this.getCurrentUser().getUserId().equals(this.user.getUserId());
	}
	
	public String getMyMusicCSS() {
		return this.isCurrentProfile() ? "active" : "";
	}

	public User getUser() {
		return user;
	}

	public List<FeedPost> getFeed() {
		return feed;
	}

	public String getProfileURL() {
		return "/profile/"+this.user.getUserId();
	}


}
