package com.somedamnmusic.pages;

import java.text.ParseException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Post;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.exception.DatabaseException;
import com.somedamnmusic.entities.Entities.MusicPost;
import com.somedamnmusic.session.Session;

@At("/postmusic")
public class PostMusicComplete {
	private final Session session;
	private final DatabaseService db;
	
	
	private String youtubeURL;
	
	private String description;
	private String returnURL;
	
	@Inject
	public PostMusicComplete(Session session, DatabaseService db) {
		this.session = session;
		this.db = db;
	}
	
	@Post
	public String post() {
		if(validate()) {
			if(StringUtils.isNotBlank(youtubeURL)) {
				try {
					processYoutube();
				} catch (DatabaseException e) {
					e.printStackTrace(); // TODO log
				}
			}
			
		}
		
		return returnURL;
	}
	
	private void processYoutube() throws DatabaseException {
		try {
			String youtubeId = parseYoutubeURL(youtubeURL);
			
			MusicPost.Builder newPost = MusicPost.newBuilder();
			
			newPost.setId(db.getRandomkey());
			newPost.setPosterId(session.getUser().getUserId());
			newPost.setDescription(description);
			newPost.setYoutubeId(youtubeId);
			
			MusicPost newPostCompleted = newPost.build();
			
			session.setJustPostedMusic(newPostCompleted);
		} catch (ParseException e) {
			// TODO warn user
			e.printStackTrace(); // TODO log
		}
	}
	
	private String parseYoutubeURL(String youtubeFullURL) throws ParseException {
		if(StringUtils.isBlank(youtubeFullURL)) {
			throw new ParseException(youtubeFullURL, 0);
		}
		
		if(!youtubeFullURL.toLowerCase().contains("v=")) {
			throw new ParseException(youtubeFullURL, 0);
		}
		
		final int idIndex = youtubeFullURL.toLowerCase().indexOf("v=") + 2;
		
		String youtubeEndURL;
		try {
			youtubeEndURL = youtubeFullURL.substring(idIndex);
		} catch(IndexOutOfBoundsException e) {
			throw new ParseException(youtubeFullURL, idIndex);
		}
		
		if(!youtubeEndURL.contains(StringEscapeUtils.escapeHtml4("&"))) {
			return youtubeEndURL;
		}
		
		int endIndex = youtubeEndURL.indexOf(StringEscapeUtils.escapeHtml4("&"));
		
		try {
			return youtubeEndURL.substring(0, endIndex);
		} catch(IndexOutOfBoundsException e) {
			throw new ParseException(youtubeFullURL, idIndex);
		}
	}

	private boolean validate() {
		return StringUtils.isNotBlank(youtubeURL)
				
				&& StringUtils.isNotBlank(returnURL)
				
				&& session.getUser() != null;
	}

	public String getYoutubeURL() {
		return youtubeURL;
	}

	public void setYoutubeURL(String youtubeURL) {
		this.youtubeURL = StringEscapeUtils.escapeHtml4(youtubeURL);
	}

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = StringEscapeUtils.escapeHtml4(returnURL);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = StringEscapeUtils.escapeHtml4(description);
	}

}
