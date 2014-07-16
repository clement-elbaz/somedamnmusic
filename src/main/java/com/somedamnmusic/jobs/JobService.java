package com.somedamnmusic.jobs;

public interface JobService {
	
	/**
	 * launch a thread job.
	 * 
	 * @param runnable
	 * @return
	 */
	void launchJob(Runnable runnable);

}
