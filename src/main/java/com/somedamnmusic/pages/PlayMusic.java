package com.somedamnmusic.pages;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Get;
import com.somedamnmusic.apis.ListenService;
import com.somedamnmusic.apis.UserService;
import com.somedamnmusic.apis.exception.NoMusicPostException;
import com.somedamnmusic.apis.exception.UnexplainableListenServiceException;
import com.somedamnmusic.entities.Entities.MusicPost;
import com.somedamnmusic.session.Session;

@At("/listen/:random/track/:feedId/:trackId")
public class PlayMusic extends UserPage {
	private final ListenService listenService;
	private boolean notFound;
	private MusicPost musicPost;
	private String feedId;
	private int trackId;
	private boolean random;
	

	private int previousTrackId;
	
	@Inject
	public PlayMusic(ListenService listenService, Session session, UserService userService) {
		super(session, userService);
		this.listenService = listenService;
	}
	
	@Get
	public void get(@Named("random")String randomAsString, @Named("feedId")String feedId, @Named("trackId")String trackId) {
		this.feedId = feedId;
		
		try {
			this.musicPost = listenService.getMusic(feedId, trackId);
			this.trackId = Integer.parseInt(trackId);
			this.previousTrackId = this.trackId + 1;
			this.random = this.computeRandom(randomAsString);
		} catch (UnexplainableListenServiceException e) {
			// TODO log
			e.printStackTrace();
			this.notFound = true;
		} catch (NoMusicPostException e) {
			this.notFound = true;
		}
	}

	private boolean computeRandom(String randomAsString) {
		return StringUtils.equals("random", randomAsString);
	}

	public boolean isNotFound() {
		return notFound;
	}

	public MusicPost getMusicPost() {
		return musicPost;
	}
	
	public String getFeedId() {
		return feedId;
	}

	public int getTrackId() {
		return trackId;
	}

	public int getPreviousTrackId() {
		return previousTrackId;
	}

	public boolean isRandom() {
		return random;
	}
	
	public String getRandomOrSequence() {
		if(this.isRandom()) {
			return "random";
		}
		return "sequence";
	}
}
