package com.somedamnmusic.jobs;


public class SimpleJobService implements JobService {

	public void launchJob(Runnable runnable) {
		// no thread pool in the beginning
		new Thread(runnable).start();
	}

}
