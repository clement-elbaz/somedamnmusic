package com.somedamnmusic.session;

import com.somedamnmusic.entities.Entities.MusicPost;
import com.somedamnmusic.entities.Entities.User;

public class Session {
	
	private User user;
	private MusicPost justPostedMusic;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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
}
