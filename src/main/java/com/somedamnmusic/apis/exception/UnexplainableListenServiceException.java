package com.somedamnmusic.apis.exception;

public class UnexplainableListenServiceException extends Exception {

	public UnexplainableListenServiceException(
			UnexplainableFeedServiceException e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5651058489617268961L;

}
