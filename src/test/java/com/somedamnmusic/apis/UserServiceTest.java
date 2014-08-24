package com.somedamnmusic.apis;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.apis.exception.UnexplainableUserServiceException;
import com.somedamnmusic.dumb.DumbDatabase;
import com.somedamnmusic.entities.Entities.User;

public class UserServiceTest {
	UserService userService;
	
	@Before
	public void before() {
		Injector injector = Guice.createInjector(new AbstractModule() {

			@Override
			public void configure() {
				bind(DatabaseService.class).to(DumbDatabase.class);
			}
			
		});
		
		this.userService = injector.getInstance(UserService.class);
	}
	
	@Test
	public void userServiceTest() {
		Assert.assertNotNull(userService);
		
		User user = this.provideUser("toto@toto.com");
		
		try {
			userService.storeUser(user);
		} catch (UnexplainableUserServiceException e) {
			Assert.fail(e.toString());
		}
		
		User userRetrieved;
		try {
			userRetrieved = userService.getUserFromEmail("toto@toto.com");
			
			Assert.assertEquals(user, userRetrieved);
			
			User userRetrieved2 = userService.getUserFromId(user.getUserId());
			
			Assert.assertEquals(user, userRetrieved2);
		} catch (NoUserException e) {
			Assert.fail(e.toString());
		} catch (UnexplainableUserServiceException e) {
			Assert.fail(e.toString());
		}
		
		
	}

	private User provideUser(String email) {
		User.Builder user = User.newBuilder();
		
		user.setUserId("some_id");
		user.setEmail(email);
		user.setFirstName("Bob");
		user.setLastName("Morane");
		return user.build();
	}

}
