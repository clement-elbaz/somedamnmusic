package com.somedamnmusic.apis.exception;

public class UnexplainableFeedServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3446544843936450281L;
	
	public UnexplainableFeedServiceException(Exception e) {
		super(e);
	}

	public UnexplainableFeedServiceException() {
		super();
	}

}
