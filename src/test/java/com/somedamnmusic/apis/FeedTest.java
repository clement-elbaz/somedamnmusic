package com.somedamnmusic.apis;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.apis.exception.UnexplainableFeedServiceException;
import com.somedamnmusic.apis.exception.UnexplainableUserServiceException;
import com.somedamnmusic.dumb.DumbDatabase;
import com.somedamnmusic.entities.Entities.MusicPost;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.jobs.JobService;
import com.somedamnmusic.jobs.PostMusicJob;
import com.somedamnmusic.jobs.PostMusicJob.PostMusicJobFactory;
import com.somedamnmusic.jobs.PostMusicOnFeedJob;
import com.somedamnmusic.jobs.PostMusicOnFeedJob.PostMusicOnFeedJobFactory;
import com.somedamnmusic.pages.DisplayFeed.FeedPost;
import com.somedamnmusic.session.Session;

public class FeedTest {
	FeedService feedService;
	UserService userService;
	PostMusicJobFactory postMusicJobFactory;
	Session session;

	@Before
	public void before() {
		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			public void configure() {
				bind(DatabaseService.class).to(DumbDatabase.class);

				install(new FactoryModuleBuilder()
				.implement(PostMusicJob.class, PostMusicJob.class)
				.build(PostMusicJobFactory.class));
				
				install(new FactoryModuleBuilder()
                .implement(PostMusicOnFeedJob.class, PostMusicOnFeedJob.class)
                .build(PostMusicOnFeedJobFactory.class));
				
				bind(JobService.class).to(SynchronousJobService.class);
				
				bind(Session.class).asEagerSingleton();
			}

		});

		this.userService = injector.getInstance(UserService.class);
		this.feedService = injector.getInstance(FeedService.class);
		this.postMusicJobFactory = injector.getInstance(PostMusicJobFactory.class);
		this.session = injector.getInstance(Session.class);


	}

	@Test
	public void feedTest() {
		User user;
		try {
			user = this.provideUser();
			userService.storeUser(user);
			session.setUserId(user.getUserId());
		} catch (UnexplainableFeedServiceException e) {
			Assert.fail(e.toString());
			return;
		} catch (UnexplainableUserServiceException e) {
			Assert.fail(e.toString());
			return;
		}

		MusicPost musicPost = this.provideMusicPost(user);

		PostMusicJob postMusicJob = postMusicJobFactory.create(musicPost);

		postMusicJob.run();

		// update user
		try {
			user = userService.getUserFromId(user.getUserId());
		} catch (NoUserException e) {
			Assert.fail(e.toString());
		} catch (UnexplainableUserServiceException e) {
			Assert.fail(e.toString());
		}

		List<FeedPost> feed;
		try {
			feed = feedService.getFeed(user.getWhatIPostFeedId());
			
			Assert.assertNotNull(feed);
			Assert.assertEquals(1, feed.size());

			Assert.assertEquals("00000000", feed.get(0).youtubeId);
			Assert.assertTrue(feed.get(0).isYoutube);
		} catch (UnexplainableFeedServiceException e) {
			Assert.fail(e.toString());
		}
	}

	private MusicPost provideMusicPost(User user) {
		MusicPost.Builder musicPost = MusicPost.newBuilder();

		musicPost.setId("some_post_id");
		musicPost.setPosterId(user.getUserId());
		musicPost.setDescription("some cool vid");
		musicPost.setYoutubeId("00000000");

		return musicPost.build();
	}

	private User provideUser() throws UnexplainableFeedServiceException {
		User.Builder user = User.newBuilder();

		user.setUserId("some_id");
		user.setEmail("toto@toto.com");
		user.setFirstName("Bob");
		user.setLastName("Morane");
		user.setWhatIFollowFeedId(feedService.createFeed());
		user.setWhatIPostFeedId(feedService.createFeed());
		return user.build();
	}

}
