package com.somedamnmusic.jobs;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.FeedService;
import com.somedamnmusic.apis.UserService;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.apis.exception.UnexplainableUserServiceException;
import com.somedamnmusic.database.UnexplainableDatabaseServiceException;
import com.somedamnmusic.entities.Entities.MusicPost;
import com.somedamnmusic.entities.Entities.Topic;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.jobs.PostMusicOnFeedJob.PostMusicOnFeedJobFactory;
import com.somedamnmusic.session.Session;

public class PostMusicJob implements Runnable {
	private final DatabaseService db;
	private final UserService userService;
	private final PostMusicOnFeedJobFactory postMusicOnFeedJobFactory;
	private final JobService jobService;
	private final MusicPost musicPost;
	private final String currentUserId;
	
	private User currentUser;
	
	@Inject
	public PostMusicJob(DatabaseService db, UserService userService, PostMusicOnFeedJobFactory postMusicOnFeedJobFactory, JobService jobService, Session session, @Assisted MusicPost musicPost) {
		this.db = db;
		this.userService = userService;
		this.postMusicOnFeedJobFactory = postMusicOnFeedJobFactory;
		this.jobService = jobService;
		this.currentUserId = session.getUserId();
		this.musicPost = musicPost;
	}

	@Override
	public void run() {
		try {
			db.set(musicPost.getId(), musicPost.toByteString());
			currentUser = userService.getUserFromId(currentUserId);
			
			Topic topic = this.createNewTopic();
			
			PostMusicOnFeedJob job = postMusicOnFeedJobFactory.create(currentUser.getWhatIPostFeedId(), topic);
			jobService.launchJob(job);
			jobService.launchJob(postMusicOnFeedJobFactory.create(currentUser.getWhatIFollowFeedId(), topic));
			jobService.launchJob(postMusicOnFeedJobFactory.create(FeedService.PUBLIC_GLOBAL_FEED, topic));
			
			for(String userId : currentUser.getFollowersList()) {
				User user = userService.getUserFromId(userId);
				jobService.launchJob(postMusicOnFeedJobFactory.create(user.getWhatIFollowFeedId(), topic));
			}
		} catch (NoUserException e) {
			e.printStackTrace(); // TODO log
		} catch (UnexplainableDatabaseServiceException e) {
			e.printStackTrace(); // TODO log
		} catch (UnexplainableUserServiceException e) {
			e.printStackTrace(); // TODO log
		}
	}

	private Topic createNewTopic() throws UnexplainableDatabaseServiceException {
		Topic.Builder newTopic = Topic.newBuilder();
		newTopic.setId(db.getAvailablekey());
		newTopic.addPostIds(musicPost.getId());
		
		Topic topic = newTopic.build();
		db.set(topic.getId(), topic.toByteString());
		
		return topic;
	}

	public interface PostMusicJobFactory {
		PostMusicJob create(MusicPost musicPost);
	}
	
	

}
