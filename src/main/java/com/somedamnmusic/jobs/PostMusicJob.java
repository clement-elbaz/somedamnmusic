package com.somedamnmusic.jobs;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.FeedService;
import com.somedamnmusic.apis.FeedService.FeedType;
import com.somedamnmusic.entities.Entities.Feed;
import com.somedamnmusic.entities.Entities.MusicPost;
import com.somedamnmusic.entities.Entities.Topic;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.session.Session;

public class PostMusicJob implements Runnable {
	private final DatabaseService db;
	private final Session session;
	private final MusicPost musicPost;
	
	@Inject
	public PostMusicJob(DatabaseService db, Session session, @Assisted MusicPost musicPost) {
		this.db = db;
		this.session = session;
		this.musicPost = musicPost;
	}

	@Override
	public void run() {
		db.set(musicPost.getId(), musicPost.toByteString());
		
		Topic topic = this.createNewTopic();
		
		this.postOnFeed(topic, this.session.getUser().getWhatIPostFeedId(), FeedType.WHAT_I_POST);
		this.postOnFeed(topic, this.session.getUser().getWhatIFollowFeedId(), FeedType.WHAT_I_FOLLOW);
		this.postOnFeed(topic, FeedService.PUBLIC_GLOBAL_FEED, FeedType.PUBLIC);
	}
	
	private Topic createNewTopic() {
		Topic.Builder newTopic = Topic.newBuilder();
		newTopic.setId(db.getRandomkey());
		newTopic.addPostIds(musicPost.getId());
		
		Topic topic = newTopic.build();
		
		db.set(topic.getId(), topic.toByteString());
		
		return topic;
	}
	
	private void postOnFeed(Topic topic, String feedId, FeedType feedType) {
		Feed feed = this.getFeed(feedId, feedType);
		
		Feed.Builder updatedFeed = feed.toBuilder();
		
		updatedFeed.addTopicIds(topic.getId());
		
		db.set(feed.getId(), updatedFeed.build().toByteString());
	}
	
	private Feed getFeed(String feedId, FeedType feedType) {
		boolean needToCreateFeed;
		Feed feed = null;
		if(StringUtils.isNotBlank(feedId)) {
			ByteString feedContent = db.get(feedId);
			
			
			needToCreateFeed = false;
			if(feedContent != null) {
				try {
					feed = Feed.parseFrom(feedContent);
				} catch (InvalidProtocolBufferException e) {
					needToCreateFeed = true;
				}
			} else {
				needToCreateFeed = true;
			}
		} else {
			needToCreateFeed = true;
		}
		
		
		if(needToCreateFeed) {
			Feed.Builder newFeed = Feed.newBuilder();
			if(feedType.isConstant()) {
				newFeed.setId(feedId);
			} else {
				newFeed.setId(db.getRandomkey());
			}
			
			feed = newFeed.build();
			db.set(feed.getId(), feed.toByteString());
			if(!FeedType.PUBLIC.equals(feedType)) {
				this.updateUser(feed, feedType);
			}
		}
		
		return feed;
	}
	
	private void updateUser(Feed feed, FeedType feedType) {
		User.Builder updatedUser = this.session.getUser().toBuilder();
		
		if(FeedType.WHAT_I_FOLLOW.equals(feedType)) {
			updatedUser.setWhatIFollowFeedId(feed.getId());
		}
		
		if(FeedType.WHAT_I_POST.equals(feedType)) {
			updatedUser.setWhatIPostFeedId(feed.getId());
		}
		
		User user = updatedUser.build();
		
		db.set(user.getEmail(), user.toByteString());
		session.setUser(user);
	}

	public interface PostMusicJobFactory {
		PostMusicJob create(MusicPost musicPost);
	}
	
	

}
