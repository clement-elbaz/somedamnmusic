package com.somedamnmusic.apis;

import com.google.protobuf.ByteString;
import com.somedamnmusic.apis.exception.NoResultException;
import com.somedamnmusic.database.UnexplainableDatabaseServiceException;

public interface DatabaseService {
	
	/**
	 * Get some data.
	 * 
	 * @param key
	 * @return
	 */
	ByteString get(String key) throws NoResultException, UnexplainableDatabaseServiceException;
	
	/**
	 * Remove data.
	 * 
	 * @param key
	 */
	void remove(String key) throws UnexplainableDatabaseServiceException;
	
	/**
	 * Set some data.
	 * 
	 * @param key
	 * @param content
	 */
	void set (String key, ByteString content) throws UnexplainableDatabaseServiceException;
	
	/**
	 * Set the expiration time of some data
	 * @param key
	 * @param seconds
	 * @throws UnexplainableDatabaseServiceException
	 */
	void expires(String key, int seconds) throws UnexplainableDatabaseServiceException;
	
	/**
	 * Get a brand new random available key.
	 * @return
	 */
	String getAvailablekey() throws UnexplainableDatabaseServiceException;

}
