package com.somedamnmusic.apis;

import com.google.inject.Inject;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.somedamnmusic.apis.exception.DatabaseException;
import com.somedamnmusic.apis.exception.NoUserException;
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
	 */
	public User getUserFromId(String id) throws NoUserException {
		ByteString content;
		try {
			content = db.get(id);
			if(content != null) {
				return User.parseFrom(content);
			} else {
				throw new NoUserException();
			}
		} catch (DatabaseException e) {
			throw new NoUserException(e);
		} catch (InvalidProtocolBufferException e) {
			throw new NoUserException(e);
		}
		
	}
	
	/**
	 * Get a user from his email.
	 * 
	 * @param email
	 * @return
	 * @throws NoUserException
	 */
	public User getUserFromEmail(String email) throws NoUserException {
		try {
			ByteString id = db.get(email);
			if(id != null) {
				return this.getUserFromId(id.toStringUtf8());
			}
			throw new NoUserException();
		} catch (DatabaseException e) {
			throw new NoUserException(e);
		}
	}
	
	/**
	 * Store a user. Please call asynchronously.
	 * 
	 * @param user
	 * @throws DatabaseException
	 */
	public void storeUser(User user) throws DatabaseException {
		db.set(user.getUserId(), user.toByteString());
		db.set(user.getEmail(), ByteString.copyFromUtf8(user.getUserId()));
	}

}
