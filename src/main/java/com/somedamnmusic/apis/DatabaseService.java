package com.somedamnmusic.apis;

import com.google.protobuf.ByteString;

public interface DatabaseService {
	
	/**
	 * Getter.
	 * 
	 * @param key
	 * @return
	 */
	ByteString get(String key);
	
	/**
	 * Setter.
	 * 
	 * @param key
	 * @param content
	 */
	void set (String key, ByteString content);
	
	/**
	 * Get a brand new random key.
	 * @return
	 */
	String getRandomkey();

}
