package com.somedamnmusic.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.sitebricks.rendering.EmbedAs;

@EmbedAs("DisplayFeed")
public class DisplayFeed {
	private List<FeedPost> feedPosts = new ArrayList<FeedPost>();

	public static class FeedPost {
		public String posterFirstname;
		public String posterLastname;
		public String posterId;
		public String description;
		public boolean isYoutube;
		public String youtubeId;
	}

	public List<FeedPost> getFeedPosts() {
		return feedPosts;
	}

	public void setFeedPosts(List<FeedPost> feedPosts) {
		if(feedPosts == null) {
			this.feedPosts = new ArrayList<FeedPost>();
		} else {
			this.feedPosts = feedPosts;
			Collections.reverse(feedPosts);
		}
	}
	
	public boolean isEmpty() {
		return feedPosts.isEmpty();
	}

}
