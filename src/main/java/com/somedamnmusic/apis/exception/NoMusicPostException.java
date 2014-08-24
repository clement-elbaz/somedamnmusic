package com.somedamnmusic.apis.exception;

public class NoMusicPostException extends Exception {

	public NoMusicPostException(NoResultException e) {
		super(e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5624160052908694144L;

}
