package com.somedamnmusic.pages;

import com.google.inject.Inject;
import com.google.sitebricks.At;
import com.google.sitebricks.http.Post;
import com.somedamnmusic.jobs.JobService;
import com.somedamnmusic.jobs.LoginJob;
import com.somedamnmusic.jobs.LoginJob.LoginJobFactory;

@At("/signin")
public class SigninComponent {
	private final JobService jobService;
	private final LoginJobFactory loginJobFactory;
	
	private String email;
	
	@Inject
	public SigninComponent(JobService jobService, LoginJobFactory loginJobFactory) {
		this.jobService = jobService;
		this.loginJobFactory = loginJobFactory;
	}
	
	@Post
	public void post() {
		System.out.println("Attempt to log with email : "+email);
		LoginJob job = loginJobFactory.create(email);
		jobService.launchJob(job);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
