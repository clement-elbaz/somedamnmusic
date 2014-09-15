package com.somedamnmusic.pages;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Post;
import com.somedamnmusic.apis.ListenService;
import com.somedamnmusic.apis.exception.NoMusicPostException;
import com.somedamnmusic.apis.exception.UnexplainableListenServiceException;

@At("/listen/next/:feedId")
public class NextTrack {
	private final ListenService listenService;
	
	private String trackId;
	private String random;
	
	@Inject
	public NextTrack(ListenService listenService) {
		this.listenService = listenService;
	}
	
	@Post
	public String post(@Named("feedId")String feedId) {
		final boolean randomBool = Boolean.parseBoolean(random);
		try {
			final int trackIdInt = Integer.parseInt(trackId);
			final int nextTrackId = this.listenService.nextTrack(feedId, trackIdInt, randomBool);
		
			return "/listen/track/"+feedId+"/"+nextTrackId;
		} catch(NumberFormatException e) {
			return "http://youlittlehacker.com";
		} catch (NoMusicPostException e) {
			return "/listen/end";
		} catch (UnexplainableListenServiceException e) {
			// TODO log
			e.printStackTrace();
			return "500.html";
		}
	}
	
	public String getRandom() {
		return random;
	}
	public void setRandom(String random) {
		this.random = random;
	}
	public String getTrackId() {
		return trackId;
	}
	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}
}
