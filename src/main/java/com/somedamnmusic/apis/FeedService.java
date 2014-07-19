package com.somedamnmusic.apis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.somedamnmusic.apis.exception.DatabaseException;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.entities.Entities.Feed;
import com.somedamnmusic.entities.Entities.MusicPost;
import com.somedamnmusic.entities.Entities.Topic;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.jobs.JobService;
import com.somedamnmusic.jobs.PostMusicJob;
import com.somedamnmusic.jobs.PostMusicJob.PostMusicJobFactory;
import com.somedamnmusic.pages.DisplayFeed.FeedPost;
import com.somedamnmusic.session.Session;

public class FeedService {
	public static final String PUBLIC_GLOBAL_FEED = "public_global_feed";

	private final DatabaseService db;
	private final UserService userService;
	private final Session session;
	private final JobService jobService;
	private final PostMusicJobFactory postMusicJobFactory;

	@Inject
	public FeedService(DatabaseService db, UserService userService, Session session, JobService jobService, PostMusicJobFactory postMusicJobFactory) {
		this.db = db;
		this.userService = userService;
		this.session = session;
		this.jobService = jobService;
		this.postMusicJobFactory = postMusicJobFactory;
	}

	public List<FeedPost> getFeed(String feedId) {
		MusicPost justPostedMusic = session.getJustPostedMusic();
		if(!validate(feedId, justPostedMusic)) {
			return null;
		}
		List<FeedPost> feedPosts = new ArrayList<FeedPost>();
		ByteString feedContent;
		try {
			feedContent = db.get(feedId);
			if(feedContent != null) {
				try {
					Feed feed = Feed.parseFrom(feedContent);
					for(int i = 0; i < feed.getTopicIdsCount(); i++) {
						Topic topic = this.getTopic(feed.getTopicIds(i));
						if(topic != null) {
							MusicPost musicPost = this.getMusicPost(topic.getPostIds(0));
							if(musicPost != null) {
								processMusicPost(musicPost, feedPosts);
							}
						}
					}
				} catch (InvalidProtocolBufferException e) {
					// do nothing
				}
			}
		} catch (DatabaseException e1) {
			// do nothing
		}


		if(justPostedMusic != null) {
			PostMusicJob postJob = postMusicJobFactory.create(justPostedMusic);
			jobService.launchJob(postJob);
			processMusicPost(justPostedMusic, feedPosts);
		}

		return feedPosts;
	}

	private void processMusicPost(MusicPost musicPost, List<FeedPost> feedPosts) {
		try {
			User posterUser = userService.getUserFromId(musicPost.getPosterId());

			FeedPost feedPost = new FeedPost();

			feedPost.posterFirstname = posterUser.getFirstName();
			feedPost.posterLastname = posterUser.getLastName();
			feedPost.description = musicPost.getDescription();
			feedPost.isYoutube = StringUtils.isNotBlank(musicPost.getYoutubeId());
			feedPost.youtubeId = musicPost.getYoutubeId();

			feedPosts.add(feedPost);
		} catch (NoUserException e) {
			// do nothing
		}
	}

	private Topic getTopic(String topicId) {
		ByteString content;
		try {
			content = db.get(topicId);
			if(content != null) {
				return Topic.parseFrom(content);
			}
		} catch (DatabaseException e1) {
			// do nothing
		} catch (InvalidProtocolBufferException e) {
			// do nothing
		}

		return null;
	}

	private MusicPost getMusicPost(String postId) {
		try {
			ByteString content = db.get(postId);
			if(content != null) {
				return MusicPost.parseFrom(content);
			}
		} catch (InvalidProtocolBufferException e) {
			// do nothing
		} catch (DatabaseException e) {
			// do nothing
		}
		return null;
	}

	private boolean validate(String feedId, MusicPost justPostedMusic) {
		return StringUtils.isNotBlank(feedId) || justPostedMusic != null;
	}

	public static enum FeedType {
		WHAT_I_FOLLOW(false, false),
		WHAT_I_POST(true, false),
		PUBLIC(false, true);

		private final boolean persistent;
		private final boolean constant;

		private FeedType(boolean persistent, boolean constant) {
			this.persistent = persistent;
			this.constant = constant;
		}

		public boolean isPersistent() {
			return persistent;
		}

		public boolean isConstant() {
			return constant;
		}
	}

}
