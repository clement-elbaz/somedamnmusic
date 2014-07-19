package com.somedamnmusic.dumb;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Singleton;
import com.google.protobuf.ByteString;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.exception.DatabaseException;

@Singleton
public class DumbDatabase implements DatabaseService {
	private int count = 0;
	
	private Map<String, ByteString> database = new HashMap<String, ByteString>();

	public ByteString get(String key) throws DatabaseException {
		if(StringUtils.isBlank(key)) {
			throw new DatabaseException();
		}
		return database.get(key);
	}

	public void set(String key, ByteString content) throws DatabaseException {
		if(StringUtils.isBlank(key) || content == null) {
			throw new DatabaseException();
		}
		database.put(key, content);

	}

	public String getRandomkey() {
		count++;
		return "key_"+count;
	}

	public void remove(String key) throws DatabaseException {
		if(StringUtils.isBlank(key)) {
			throw new DatabaseException();
		}
		database.remove(key);
	}

}
