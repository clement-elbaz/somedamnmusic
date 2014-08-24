package com.somedamnmusic.apis.exception;

public class NoTopicException extends Exception {

	public NoTopicException(NoResultException e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7732907802959022062L;

}
