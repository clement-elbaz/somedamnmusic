package com.somedamnmusic.apis;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.dumb.DumbDatabase;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.jobs.JobService;
import com.somedamnmusic.jobs.UpdateUserJob;
import com.somedamnmusic.jobs.UpdateUserJob.UpdateUserJobFactory;

public class FollowServiceTest {
	private UserService userService;
	private FollowService followService;
	
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
			}
			
		});
		
		this.userService = injector.getInstance(UserService.class);
		this.followService = injector.getInstance(FollowService.class);
	}
	
	@Test
	public void testFollow() {
		Assert.assertNotNull(followService);
		
		User user = this.provideUser1();
		User user2 = this.provideUser2();
		
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
		
		
	}

	private User provideUser2() {
		User.Builder user = User.newBuilder();
		user.setUserId("id_2");
		user.setEmail("luke@skywalker.net");
		user.setFirstName("Luke");
		user.setLastName("Skywalker");
		
		return user.build();
	}

	private User provideUser1() {
		User.Builder user = User.newBuilder();
		user.setUserId("id_1");
		user.setEmail("bob@morane.com");
		user.setFirstName("Bob");
		user.setLastName("Morane");
		
		return user.build();
	}

}
