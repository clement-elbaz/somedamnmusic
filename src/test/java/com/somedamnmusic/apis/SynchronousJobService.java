package com.somedamnmusic.apis;

import com.somedamnmusic.jobs.JobService;

/**
 * A synchronous job service for testing purpose.
 * (avoiding hell on earth aka multithreaded unit tests)
 * @author Cl�ment
 *
 */
public class SynchronousJobService implements JobService {

	@Override
	public void launchJob(Runnable runnable) {
		runnable.run();

	}

}
