package com.somedamnmusic.session;

import com.somedamnmusic.entities.Entities.MusicPost;
import com.somedamnmusic.entities.Entities.User;

public class Session {

	private String userId;
	private MusicPost justPostedMusic;
	private User justFollowedUser;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public MusicPost getJustPostedMusic() {
		// we make sure the just posted music doesn't stay in session
		MusicPost result = justPostedMusic;
		justPostedMusic = null;
		return result;
	}

	public void setJustPostedMusic(MusicPost justPostedMusic) {
		this.justPostedMusic = justPostedMusic;
	}

	public void setJustFollowedUser(User followedUser) {
		this.justFollowedUser = followedUser;
	}
	
	public User getJustFollowedUser() {
		// we make sure the just followed user doesn't stay in session
		User result = justFollowedUser;
		justFollowedUser = null;
		return result;
	}
}
