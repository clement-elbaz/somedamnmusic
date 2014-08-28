package com.somedamnmusic.session;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.protobuf.ByteString;
import com.somedamnmusic.apis.DatabaseService;
import com.somedamnmusic.apis.exception.NoResultException;
import com.somedamnmusic.apis.exception.NoUserException;
import com.somedamnmusic.database.UnexplainableDatabaseServiceException;

@Singleton
public class SessionFilter implements Filter {
	private static final String COOKIE_TOKEN = "SDM_TOKEN";
	
	private static final int COOKIE_DURATION = 60 * 60 * 24 * 60; // two months in seconds
	
	private final Provider<Session> sessionProvider;
	private final DatabaseService databaseService;
	
	
	@Inject
	public SessionFilter(Provider<Session> sessionProvider, DatabaseService databaseService) {
		this.sessionProvider = sessionProvider;
		this.databaseService = databaseService;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// do nothing

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		
		Session session = sessionProvider.get();
		
		boolean setCookie = false;
		try {
			String userIdFromCookie = this.extractUserIdFromTokenCookie(httpRequest);
			if(StringUtils.isBlank(session.getUserId())) {
				session.setUserId(userIdFromCookie);
				// if user has a valid cookie but no session, it is a good time to renew his cookie
				setCookie = true;
			}
		} catch(NoUserException e) {
			if(StringUtils.isNotBlank(session.getUserId())) {
				// if session is set but no cookie, it is a log in
				setCookie = true;
			}
		} catch (UnexplainableDatabaseServiceException e) {
			e.printStackTrace(); // TODO log
		}
		
		if(setCookie) {
			try {
				this.setTokenCookie(httpRequest, httpResponse, session.getUserId());
			} catch (UnexplainableDatabaseServiceException e) {
				e.printStackTrace(); // TODO log
			}
		}
		
		chain.doFilter(request, response);
	}

	private void setTokenCookie(HttpServletRequest request,
			HttpServletResponse response, String userId) throws UnexplainableDatabaseServiceException {
		try {
			String token = this.extractTokenCookie(request);
			databaseService.remove(token);
		} catch (NoUserException e) {
			// do nothing
		}
		
		String newToken = this.databaseService.getAvailablekey();
		this.databaseService.set(newToken, ByteString.copyFromUtf8(userId));
		this.databaseService.expires(newToken, COOKIE_DURATION);
		
		Cookie newCookie = new Cookie(COOKIE_TOKEN, newToken);
		newCookie.setMaxAge(COOKIE_DURATION);
		
		response.addCookie(newCookie);
		
	}

	private String extractUserIdFromTokenCookie(HttpServletRequest request) throws NoUserException, UnexplainableDatabaseServiceException {
		try {
			String token = this.extractTokenCookie(request);
			return this.databaseService.get(token).toStringUtf8();
		} catch (NoResultException e) {
			throw new NoUserException(e);
		}
	}
	
	private String extractTokenCookie(HttpServletRequest request) throws NoUserException {
		if(request.getCookies() != null) {
			for(int i = 0 ; i < request.getCookies().length; i++) {
				Cookie cookie = request.getCookies()[i];
				if(COOKIE_TOKEN.equals(cookie.getName())) {
					String token = cookie.getValue();
					if(StringUtils.isNotBlank(token)) {
						return token;
					}
				}
			}
		}
		throw new NoUserException();
	}

	@Override
	public void destroy() {
		// destroy filter

	}

}
