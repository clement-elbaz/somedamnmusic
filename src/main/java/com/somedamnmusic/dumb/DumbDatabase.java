package com.somedamnmusic.dumb;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Singleton;
import com.google.protobuf.ByteString;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.exception.NoResultException;
import com.somedamnmusic.database.UnexplainableDatabaseServiceException;

@Singleton
public class DumbDatabase implements DatabaseService {
	private int count = 0;
	
	private Map<String, ByteString> database = new HashMap<String, ByteString>();

	public ByteString get(String key) throws NoResultException, UnexplainableDatabaseServiceException {
		if(StringUtils.isBlank(key)) {
			throw new UnexplainableDatabaseServiceException();
		}
		ByteString result = database.get(key);
		if(result == null) {
			throw new NoResultException();
		}
		return result;
	}

	public void set(String key, ByteString content) throws UnexplainableDatabaseServiceException {
		if(StringUtils.isBlank(key) || content == null) {
			throw new UnexplainableDatabaseServiceException();
		}
		database.put(key, content);

	}

	public String getAvailablekey() {
		count++;
		return "key_"+count;
	}

	public void remove(String key) throws UnexplainableDatabaseServiceException {
		if(StringUtils.isBlank(key)) {
			throw new UnexplainableDatabaseServiceException();
		}
		database.remove(key);
	}

}
