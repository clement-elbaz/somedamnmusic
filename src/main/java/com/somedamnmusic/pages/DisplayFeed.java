package com.somedamnmusic.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.sitebricks.rendering.EmbedAs;

@EmbedAs("DisplayFeed")
public class DisplayFeed {
	private FormattedFeed formattedFeed;
	
	public static class FormattedFeed {
		public  List<FeedPost> feedPosts = new ArrayList<FeedPost>();
		public String feedId;
		public String firstTrackNumber;
	}

	public static class FeedPost {
		public String posterFirstname;
		public String posterLastname;
		public String posterId;
		public String description;
		public boolean isYoutube;
		public String youtubeId;
	}

	public List<FeedPost> getFeedPosts() {
		return formattedFeed.feedPosts;
	}

	public void setFeed(FormattedFeed formattedFeed) {
		if(formattedFeed == null) {
			this.formattedFeed = new FormattedFeed();
			this.formattedFeed.feedPosts = new ArrayList<FeedPost>();
		} else {
			this.formattedFeed = formattedFeed;
			Collections.reverse(this.formattedFeed.feedPosts);
		}
	}
	
	public boolean isEmpty() {
		return formattedFeed.feedPosts.isEmpty();
	}

	public String getFeedId() {
		return formattedFeed.feedId;
	}

	public String getFirstTrackNumber() {
		return formattedFeed.firstTrackNumber;
	}

}
