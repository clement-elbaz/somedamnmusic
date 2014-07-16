package com.somedamnmusic.pages;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Get;
import com.somedamnmusic.jobs.JobService;
import com.somedamnmusic.jobs.LoginJob;
import com.somedamnmusic.jobs.LoginJob.LoginJobFactory;

@At("/login/:email")
public class LoginPage {
	private final JobService jobService;
	private final LoginJobFactory loginJobFactory;
	
	@Inject
	public LoginPage(JobService jobService, LoginJobFactory loginJobFactory) {
		this.jobService = jobService;
		this.loginJobFactory = loginJobFactory;
	}
	
	@Get
	public void get(@Named("email")String email) {
		LoginJob job = loginJobFactory.create(email);
		jobService.launchJob(job);
	}
}
