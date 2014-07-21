package com.somedamnmusic.jobs;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.FeedService;
import com.somedamnmusic.apis.exception.DatabaseException;
import com.somedamnmusic.apis.exception.NoFeedException;
import com.somedamnmusic.entities.Entities.Feed;
import com.somedamnmusic.entities.Entities.Topic;

public class PostMusicOnFeedJob implements Runnable {
	private final FeedService feedService;
	private final DatabaseService db;
	private final String feedId;
	private final Topic topic;
	
	@Inject
	public PostMusicOnFeedJob(FeedService feedService, DatabaseService db, @Assisted String feedId, @Assisted Topic topic) {
		this.feedService = feedService;
		this.db = db;
		this.feedId = feedId;
		this.topic = topic;
	}

	@Override
	public void run() {
		try {
			Feed feed = feedService.getFeedObject(feedId);
			
			Feed.Builder updatedFeed = feed.toBuilder();

			updatedFeed.addTopicIds(topic.getId());

			db.set(feed.getId(), updatedFeed.build().toByteString());
		} catch (NoFeedException e) {
			e.printStackTrace(); // TODO log
		} catch (DatabaseException e) {
			e.printStackTrace(); // TODO log
		}
	}
	
	public static interface PostMusicOnFeedJobFactory {
		PostMusicOnFeedJob create(String feedId, Topic topic);
	}

}
