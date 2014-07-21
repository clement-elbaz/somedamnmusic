package com.somedamnmusic.apis.exception;

public class NoFeedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8853083214672588169L;
	
	public NoFeedException(Exception e) {
		super(e);
	}
	
	public NoFeedException() {
		
	}

}
