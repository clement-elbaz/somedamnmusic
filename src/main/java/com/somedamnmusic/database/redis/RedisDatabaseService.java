package com.somedamnmusic.database.redis;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisException;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.protobuf.ByteString;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.exception.NoResultException;
import com.somedamnmusic.database.UnexplainableDatabaseServiceException;

@Singleton
public class RedisDatabaseService implements DatabaseService {
	// This is useless now but will come handy if we need to do multidatabase sometime later
	public static final String CURRENT_SERVER_PREFIX = "SERVER01.";
	public static final int DEFAULT_TIMEOUT = 2000;
	private final JedisPool pool;
	
	@Inject
	public RedisDatabaseService(@Named("database.host")String host, @Named("database.port")int port, @Named("database.client")String client, @Named("database.password")String password) {
		pool = new JedisPool(new JedisPoolConfig(), host, port, DEFAULT_TIMEOUT, password, 0, client);
	}
	
	private Jedis getConnection() throws UnexplainableDatabaseServiceException {
		try  {
			return this.pool.getResource();
		} catch(JedisException e) {
			throw new UnexplainableDatabaseServiceException(e);
		}
	}
	
	private void closeConn(Jedis conn) throws UnexplainableDatabaseServiceException {
		try {
			if(conn != null) {
				conn.close();
			}
		} catch(JedisException e) {
			throw new UnexplainableDatabaseServiceException(e);
		}
		
	}

	@Override
	public ByteString get(String key) throws NoResultException,
			UnexplainableDatabaseServiceException {
		if(StringUtils.isBlank(key)) {
			throw new UnexplainableDatabaseServiceException();
		}
		Jedis conn = this.getConnection();
		try {
			String result = conn.get(CURRENT_SERVER_PREFIX + key);
			if(result == null) {
				throw new NoResultException();
			}
			return ByteString.copyFromUtf8(result);
		} catch(JedisException e) {
			throw new UnexplainableDatabaseServiceException(e);
		} finally {
			this.closeConn(conn);
		}
	}

	@Override
	public void remove(String key) throws UnexplainableDatabaseServiceException {
		Jedis conn = this.getConnection();
		try {
			conn.del(CURRENT_SERVER_PREFIX + key);
		} catch(JedisException e) {
			throw new UnexplainableDatabaseServiceException(e);
		} finally {
			this.closeConn(conn);
		}
	}

	@Override
	public void set(String key, ByteString content)
			throws UnexplainableDatabaseServiceException {
		Jedis conn = this.getConnection();
		try {
			conn.set(CURRENT_SERVER_PREFIX + key, content.toStringUtf8());
		} catch(JedisException e) {
			throw new UnexplainableDatabaseServiceException(e);
		} finally {
			this.closeConn(conn);
		}
	}

	@Override
	public String getAvailablekey() throws UnexplainableDatabaseServiceException {
		while(true) {
			String result = RandomStringUtils.randomAlphanumeric(12);
			try {
				this.get(result);
			} catch (NoResultException e) {
				return result;
			}
		}
		
	}

	@Override
	public void expires(String key, int seconds)
			throws UnexplainableDatabaseServiceException {
		Jedis conn = this.getConnection();
		try {
			conn.expire(key, seconds);
		} catch(JedisException e) {
			throw new UnexplainableDatabaseServiceException(e);
		} finally {
			this.closeConn(conn);
		}
	}

}
