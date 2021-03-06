package com.somedamnmusic.apis;

import java.util.Random;

import com.google.inject.Inject;
import com.somedamnmusic.apis.exception.NoFeedException;
import com.somedamnmusic.apis.exception.NoMusicPostException;
import com.somedamnmusic.apis.exception.NoTopicException;
import com.somedamnmusic.apis.exception.UnexplainableFeedServiceException;
import com.somedamnmusic.apis.exception.UnexplainableListenServiceException;
import com.somedamnmusic.entities.Entities.Feed;
import com.somedamnmusic.entities.Entities.MusicPost;
import com.somedamnmusic.entities.Entities.Topic;

public class ListenService {
	private final FeedService feedService;

	@Inject
	public ListenService(FeedService feedService) {
		this.feedService = feedService;
	}


	public MusicPost getMusic(String feedId, String trackIdString) throws NoMusicPostException, UnexplainableListenServiceException {
		try {
			Feed feed = this.feedService.getFeedObject(feedId);
			int trackId = Integer.parseInt(trackIdString);
			String topicId = feed.getTopicIds(trackId);
			Topic topic = this.feedService.getTopic(topicId);
			// this will need to change once music commentaries are enabled
			String musicPostId = topic.getPostIds(0);
			return this.feedService.getMusicPost(musicPostId);
		} catch (UnexplainableFeedServiceException e) {
			throw new UnexplainableListenServiceException(e);
		} catch (NoFeedException e) {
			throw new NoMusicPostException(e);
		} catch(NumberFormatException e) {
			throw new NoMusicPostException(e);
		} catch(IndexOutOfBoundsException e) {
			throw new NoMusicPostException(e);
		} catch (NoTopicException e) {
			throw new NoMusicPostException(e);
		}
	}


	public int nextTrack(String feedId, int trackId, boolean random) throws NoMusicPostException, UnexplainableListenServiceException {
		if(random) {
			return this.getRandomTrack(feedId);
		} 
		
		if(trackId > 0) {
			return trackId - 1;
		} else {
			throw new NoMusicPostException();
		}
	}
	
	private int getRandomTrack(String feedId) throws UnexplainableListenServiceException {
		try {
			Feed feed = this.feedService.getFeedObject(feedId);

			return new Random().nextInt(feed.getTopicIdsCount());
		} catch (UnexplainableFeedServiceException e) {
			throw new UnexplainableListenServiceException(e);
		} catch (NoFeedException e) {
			throw new UnexplainableListenServiceException(e);
		}
	}

}
