package com.somedamnmusic.apis;

import com.google.protobuf.ByteString;
import com.somedamnmusic.apis.exception.DatabaseException;

public interface DatabaseService {
	
	/**
	 * Getter.
	 * 
	 * @param key
	 * @return
	 */
	ByteString get(String key) throws DatabaseException;
	
	/**
	 * Remove data.
	 * 
	 * @param key
	 */
	void remove(String key) throws DatabaseException;
	
	/**
	 * Setter.
	 * 
	 * @param key
	 * @param content
	 */
	void set (String key, ByteString content) throws DatabaseException;
	
	/**
	 * Get a brand new random key.
	 * @return
	 */
	String getRandomkey() throws DatabaseException;

}
