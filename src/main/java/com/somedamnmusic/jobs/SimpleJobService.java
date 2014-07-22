package com.somedamnmusic.jobs;

import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import com.google.inject.servlet.ServletScopes;

public class SimpleJobService implements JobService {

	public void launchJob(Runnable runnable) {
		// no thread pool in the beginning
		//new Thread(runnable).start();
		
		Callable<Object> callable =  Executors.callable(runnable);
		ServletScopes.continueRequest(callable, Collections.emptyMap());
		Executors.newSingleThreadExecutor().submit(callable);
	}

}
