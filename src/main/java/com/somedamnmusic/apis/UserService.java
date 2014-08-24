package com.somedamnmusic.apis;

import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.somedamnmusic.apis.exception.NoResultException;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.apis.exception.UnexplainableUserServiceException;
import com.somedamnmusic.database.UnexplainableDatabaseServiceException;
import com.somedamnmusic.entities.Entities.User;

public class UserService {
	private final DatabaseService db;
	
	@Inject
	public UserService(DatabaseService db) {
		this.db = db;
	}
	
	/**
	 * Get a user from his id.
	 * @param id
	 * @return
	 * @throws NoUserException
	 * @throws UnexplainableUserServiceException 
	 */
	public User getUserFromId(String id) throws NoUserException, UnexplainableUserServiceException {
		try {
			ByteString content = db.get(id);
			return User.parseFrom(content);
		} catch (NoResultException e) {
			throw new NoUserException(e);
		} catch (UnexplainableDatabaseServiceException e) {
			throw new UnexplainableUserServiceException(e);
		} catch (InvalidProtocolBufferException e) {
			throw new UnexplainableUserServiceException(e);
		}
		
	}
	
	/**
	 * Get a user from his email.
	 * 
	 * @param email
	 * @return
	 * @throws NoUserException
	 */
	public User getUserFromEmail(String email) throws NoUserException, UnexplainableUserServiceException {
		try {
			ByteString id = db.get(email);
			return this.getUserFromId(id.toStringUtf8());
		} catch (NoResultException e) {
			throw new NoUserException(e);
		} catch (UnexplainableDatabaseServiceException e) {
			throw new UnexplainableUserServiceException(e);
		}
	}
	
	/**
	 * Store a user. Please call asynchronously.
	 * 
	 * @param user
	 * @throws DatabaseException
	 */
	public void storeUser(User user) throws UnexplainableUserServiceException {
		try {
			db.set(user.getUserId(), user.toByteString());
			db.set(user.getEmail(), ByteString.copyFromUtf8(user.getUserId()));
		} catch (UnexplainableDatabaseServiceException e) {
			throw new UnexplainableUserServiceException(e);
		}
		
	}

}
