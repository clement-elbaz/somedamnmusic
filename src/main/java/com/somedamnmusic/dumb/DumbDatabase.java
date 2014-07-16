package com.somedamnmusic.dumb;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Singleton;
import com.google.protobuf.ByteString;
import com.somedamnmusic.apis.DatabaseService;

@Singleton
public class DumbDatabase implements DatabaseService {
	private int count = 0;
	
	private Map<String, ByteString> database = new HashMap<String, ByteString>();

	public ByteString get(String key) {
		return database.get(key);
	}

	public void set(String key, ByteString content) {
		database.put(key, content);

	}

	public String getRandomkey() {
		count++;
		return "key_"+count;
	}

	public void remove(String key) {
		database.remove(key);
	}

}
