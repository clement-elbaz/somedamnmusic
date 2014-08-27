package com.somedamnmusic.session;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class SessionFilter implements Filter {
	private final Provider<Session> sessionProvider;
	
	
	@Inject
	public SessionFilter(Provider<Session> sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// do nothing

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		Session session = sessionProvider.get();
		// TODO check session cookie
		
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// destroy filter

	}

}
