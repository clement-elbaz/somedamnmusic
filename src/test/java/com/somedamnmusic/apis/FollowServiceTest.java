package com.somedamnmusic.apis;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.somedamnmusic.apis.exception.DatabaseException;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.dumb.DumbDatabase;
import com.somedamnmusic.entities.Entities.MusicPost;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.jobs.JobService;
import com.somedamnmusic.jobs.PostMusicJob;
import com.somedamnmusic.jobs.PostMusicOnFeedJob;
import com.somedamnmusic.jobs.PostMusicJob.PostMusicJobFactory;
import com.somedamnmusic.jobs.PostMusicOnFeedJob.PostMusicOnFeedJobFactory;
import com.somedamnmusic.jobs.UpdateUserJob;
import com.somedamnmusic.jobs.UpdateUserJob.UpdateUserJobFactory;
import com.somedamnmusic.pages.DisplayFeed.FeedPost;
import com.somedamnmusic.session.Session;

public class FollowServiceTest {
	private UserService userService;
	private FollowService followService;
	private PostMusicJobFactory postMusicJobFactory;
	private JobService jobService;
	private FeedService feedService;
	private Session session;
	
	@Before
	public void before() {
		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				bind(DatabaseService.class).to(DumbDatabase.class);
				bind(JobService.class).to(SynchronousJobService.class);
				
				install(new FactoryModuleBuilder()
                .implement(UpdateUserJob.class, UpdateUserJob.class)
                .build(UpdateUserJobFactory.class));
				
				install(new FactoryModuleBuilder()
                .implement(PostMusicJob.class, PostMusicJob.class)
                .build(PostMusicJobFactory.class));
				
				install(new FactoryModuleBuilder()
                .implement(PostMusicOnFeedJob.class, PostMusicOnFeedJob.class)
                .build(PostMusicOnFeedJobFactory.class));
				
				bind(Session.class).asEagerSingleton();
			}
			
		});
		
		this.userService = injector.getInstance(UserService.class);
		this.followService = injector.getInstance(FollowService.class);
		this.postMusicJobFactory = injector.getInstance(PostMusicJobFactory.class);
		this.jobService = injector.getInstance(JobService.class);
		this.feedService = injector.getInstance(FeedService.class);
		this.session = injector.getInstance(Session.class);
	}
	
	@Test
	public void testFollow() {
		Assert.assertNotNull(followService);
		
		User user;
		User user2;
		try {
			user = this.provideUser1();
			user2 = this.provideUser2();
		} catch (DatabaseException e1) {
			Assert.fail(e1.toString());
			return;
		}
		
		session.setUserId(user2.getUserId());
		
		Assert.assertFalse(user.getFollowingsList().contains(user2.getUserId()));
		
		User justUpdatedUser = followService.followUser(user, user2);
		Assert.assertTrue(justUpdatedUser.getFollowingsList().contains(user2.getUserId()));
		
		try {
			User updatedUser1 = userService.getUserFromId(user.getUserId());
			Assert.assertTrue(updatedUser1.getFollowingsList().contains(user2.getUserId()));
		} catch (NoUserException e) {
			Assert.fail(e.toString());
		}
		try {
			User updatedUser2 = userService.getUserFromId(user2.getUserId());
			Assert.assertTrue(updatedUser2.getFollowersList().contains(user.getUserId()));
		} catch (NoUserException e) {
			Assert.fail(e.toString());
		}
		
		MusicPost musicPost = this.provideMusicPost(user2);
		
		PostMusicJob postMusicJob = this.postMusicJobFactory.create(musicPost);
		
		jobService.launchJob(postMusicJob);
		
		try {
			User updatedUser1 = userService.getUserFromId(user.getUserId());
			List<FeedPost> feed = feedService.getFeed(updatedUser1.getWhatIFollowFeedId());
			
			Assert.assertNotNull(feed);
			Assert.assertEquals(1, feed.size());
			Assert.assertEquals("0000000000", feed.get(0).youtubeId);
			
		} catch (NoUserException e) {
			Assert.fail(e.toString());
		}
		
	}

	private User provideUser2() throws DatabaseException {
		User.Builder user = User.newBuilder();
		user.setUserId("id_2");
		user.setEmail("luke@skywalker.net");
		user.setFirstName("Luke");
		user.setLastName("Skywalker");
		user.setWhatIFollowFeedId(feedService.createFeed());
		user.setWhatIPostFeedId(feedService.createFeed());
		
		return user.build();
	}

	private User provideUser1() throws DatabaseException {
		User.Builder user = User.newBuilder();
		user.setUserId("id_1");
		user.setEmail("bob@morane.com");
		user.setFirstName("Bob");
		user.setLastName("Morane");
		user.setWhatIFollowFeedId(feedService.createFeed());
		user.setWhatIPostFeedId(feedService.createFeed());
		
		return user.build();
	}
	
	private MusicPost provideMusicPost(User user) {
		MusicPost.Builder musicPost = MusicPost.newBuilder();
		musicPost.setPosterId(user.getUserId());
		musicPost.setDescription("some cool vid");
		musicPost.setId("id_music_1");
		musicPost.setYoutubeId("0000000000");

		return musicPost.build();
	}

}
