package com.somedamnmusic.apis;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.somedamnmusic.apis.exception.NoFeedException;
import com.somedamnmusic.apis.exception.NoMusicPostException;
import com.somedamnmusic.apis.exception.NoResultException;
import com.somedamnmusic.apis.exception.NoTopicException;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.apis.exception.UnexplainableFeedServiceException;
import com.somedamnmusic.apis.exception.UnexplainableUserServiceException;
import com.somedamnmusic.database.UnexplainableDatabaseServiceException;
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
	private final Provider<Session> sessionProvider;
	private final JobService jobService;
	private final PostMusicJobFactory postMusicJobFactory;

	@Inject
	public FeedService(DatabaseService db, UserService userService, Provider<Session> sessionProvider, JobService jobService, PostMusicJobFactory postMusicJobFactory) {
		this.db = db;
		this.userService = userService;
		this.sessionProvider= sessionProvider;
		this.jobService = jobService;
		this.postMusicJobFactory = postMusicJobFactory;
	}

	private void initPublicFeedIfNecessary() throws UnexplainableDatabaseServiceException {
		try {
			db.get(PUBLIC_GLOBAL_FEED);
		} catch (NoResultException e) {
			Feed.Builder publicFeed = Feed.newBuilder();
			publicFeed.setId(PUBLIC_GLOBAL_FEED);
			db.set(PUBLIC_GLOBAL_FEED, publicFeed.build().toByteString());
		}
	}

	/**
	 * Create a new feed.
	 * @return feedId
	 * @throws DatabaseException 
	 */
	public String createFeed() throws UnexplainableFeedServiceException {
		try {
			Feed.Builder feed = Feed.newBuilder();
			feed.setId(db.getAvailablekey());

			Feed feedComplete = feed.build();

			db.set(feedComplete.getId(), feedComplete.toByteString());

			return feedComplete.getId();
		} catch(UnexplainableDatabaseServiceException e) {
			throw new UnexplainableFeedServiceException(e);
		}
	}

	/**
	 * Get a feed.
	 * @param feedId
	 * @return
	 */
	public List<FeedPost> getFeed(String feedId) throws UnexplainableFeedServiceException {
		try {
			if(PUBLIC_GLOBAL_FEED.equals(feedId)) {
				this.initPublicFeedIfNecessary();
			}

			MusicPost justPostedMusic = sessionProvider.get().getJustPostedMusic();
			if(!validate(feedId, justPostedMusic)) {
				throw new UnexplainableFeedServiceException();
			}
			List<FeedPost> feedPosts = new ArrayList<FeedPost>();

			try {
				Feed feed = this.getFeedObject(feedId);
				for(int i = 0; i < feed.getTopicIdsCount(); i++) {
					try {
						Topic topic = this.getTopic(feed.getTopicIds(i));
						MusicPost musicPost = this.getMusicPost(topic.getPostIds(0));
						processMusicPost(musicPost, feedPosts);
					} catch(NoTopicException e) {
						// do nothing
					} catch (NoMusicPostException e) {
						// do nothing
					}
				}
			} catch (NoFeedException e) {
				// do nothing
			}

			if(justPostedMusic != null) {
				PostMusicJob postJob = postMusicJobFactory.create(justPostedMusic);
				jobService.launchJob(postJob);
				processMusicPost(justPostedMusic, feedPosts);
			}

			return feedPosts;
		} catch(UnexplainableDatabaseServiceException e) {
			throw new UnexplainableFeedServiceException(e);
		}
	}

	public Feed getFeedObject(String feedId) throws UnexplainableFeedServiceException, NoFeedException {
		try {
			if(PUBLIC_GLOBAL_FEED.equals(feedId)) {
				this.initPublicFeedIfNecessary();
			}

			ByteString feedContent = db.get(feedId);
			return Feed.parseFrom(feedContent);
		} catch (NoResultException e) {
			throw new NoFeedException(e);
		} catch (InvalidProtocolBufferException e) {
			throw new UnexplainableFeedServiceException(e);
		} catch(UnexplainableDatabaseServiceException e) {
			throw new UnexplainableFeedServiceException(e);
		}
	}

	private void processMusicPost(MusicPost musicPost, List<FeedPost> feedPosts) throws UnexplainableFeedServiceException {
		try {
			User posterUser = userService.getUserFromId(musicPost.getPosterId());

			FeedPost feedPost = new FeedPost();

			feedPost.posterId = posterUser.getUserId();
			feedPost.posterFirstname = posterUser.getFirstName();
			feedPost.posterLastname = posterUser.getLastName();
			feedPost.description = musicPost.getDescription();
			feedPost.isYoutube = StringUtils.isNotBlank(musicPost.getYoutubeId());
			feedPost.youtubeId = musicPost.getYoutubeId();

			feedPosts.add(feedPost);
		} catch (NoUserException e) {
			throw new UnexplainableFeedServiceException(e);
		} catch (UnexplainableUserServiceException e) {
			throw new UnexplainableFeedServiceException(e);
		}
	}

	private Topic getTopic(String topicId) throws UnexplainableFeedServiceException, NoTopicException {
		ByteString content;
		try {
			content = db.get(topicId);
			return Topic.parseFrom(content);
		} catch (UnexplainableDatabaseServiceException e1) {
			throw new UnexplainableFeedServiceException(e1);
		} catch (InvalidProtocolBufferException e) {
			throw new UnexplainableFeedServiceException(e);
		} catch (NoResultException e) {
			throw new NoTopicException(e);
		}
	}

	private MusicPost getMusicPost(String postId) throws UnexplainableFeedServiceException, NoMusicPostException {
		try {
			ByteString content = db.get(postId);
			return MusicPost.parseFrom(content);
		} catch (InvalidProtocolBufferException e) {
			throw new UnexplainableFeedServiceException(e);
		} catch (UnexplainableDatabaseServiceException e) {
			throw new UnexplainableFeedServiceException(e);
		} catch (NoResultException e) {
			throw new NoMusicPostException(e);
		}
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
