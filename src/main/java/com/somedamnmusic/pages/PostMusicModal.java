package com.somedamnmusic.pages;

import com.google.sitebricks.rendering.EmbedAs;

@EmbedAs("PostMusicModal")
public class PostMusicModal {
	
	private String returnURL;

	public String getReturnURL() {
		return returnURL;
	}

	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}

}
