package com.somedamnmusic.jobs;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.somedamnmusic.apis.UserService;
import com.somedamnmusic.apis.exception.DatabaseException;
import com.somedamnmusic.entities.Entities.User;

public class UpdateUserJob implements Runnable {
	private final UserService userService;
	private final User user;
	
	@Inject
	public UpdateUserJob(UserService userService, @Assisted User user) {
		this.userService = userService;
		this.user = user;
	}
	

	@Override
	public void run() {
		try {
			userService.storeUser(user);
		} catch (DatabaseException e) {
			// TODO log
		}
	}
	
	public static interface UpdateUserJobFactory {
		UpdateUserJob create(User user);
	}

}
