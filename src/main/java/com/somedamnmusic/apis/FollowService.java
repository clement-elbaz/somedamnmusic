package com.somedamnmusic.apis;

import com.google.inject.Inject;
import com.somedamnmusic.entities.Entities.User;
import com.somedamnmusic.jobs.JobService;
import com.somedamnmusic.jobs.UpdateUserJob;
import com.somedamnmusic.jobs.UpdateUserJob.UpdateUserJobFactory;

public class FollowService {
	private final JobService jobService;
	private final UpdateUserJobFactory updateUserJobFactory;
	
	@Inject
	public FollowService(JobService jobService, UpdateUserJobFactory updateUserJobFactory) {
		this.jobService = jobService;
		this.updateUserJobFactory = updateUserJobFactory;
	}
	
	/**
	 * Make a user follow another.
	 * @param follower follower user
	 * @param followed followed user
	 * @return updated follower user
	 */
	public User followUser(User follower, User followed) {
		User.Builder updatedFollowerBuilder = follower.toBuilder();
		User.Builder updatedFollowedBuilder = followed.toBuilder();
		
		updatedFollowerBuilder.addFollowings(followed.getUserId());
		updatedFollowedBuilder.addFollowers(follower.getUserId());
		
		User updatedFollower = updatedFollowerBuilder.build();
		User updatedFollowed = updatedFollowedBuilder.build();
		
		UpdateUserJob updateFollowerJob = updateUserJobFactory.create(updatedFollower);
		UpdateUserJob updateFollowedJob = updateUserJobFactory.create(updatedFollowed);
		
		jobService.launchJob(updateFollowerJob);
		jobService.launchJob(updateFollowedJob);
		
		return updatedFollower;
	}

}
